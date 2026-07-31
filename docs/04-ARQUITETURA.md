# 04 — Arquitetura e tecnologias

## 1. Estilo arquitetural

A primeira versão será um **monólito modular** em Java 21 com Spring Boot. O processamento pesado ocorrerá em workers do mesmo código-base, executados como processos separados e conectados por RabbitMQ.

```text
Site Next.js
    |
    | HTTPS / REST
    v
API Spring Boot
    |-- PostgreSQL
    |-- S3/MinIO
    |-- RabbitMQ
    |-- Redis
    |-- SDK documental
    |-- Validadores PDF/assinatura/QR
    `-- Conectores de emissores
```

## 2. Aplicações

### Site web

- Next.js e TypeScript.
- Autenticação via OpenID Connect.
- Cadastro/referência de pessoas.
- Criação de sessões.
- Upload e captura via navegador.
- Acompanhamento do processamento.
- Painel de revisão e auditoria conforme permissão.

### API

- Java 21 e Spring Boot.
- REST/OpenAPI.
- Spring Security.
- Bean Validation.
- Spring Data JPA ou JDBC conforme módulo.
- Flyway para migrações.
- Actuator para saúde controlada.

### Worker

- Consome mensagens persistentes.
- Carrega arquivos privados.
- Executa processador adequado.
- Persiste tentativas, verificações e resultados.
- Emite evento de conclusão e webhook.

## 3. Módulos

```text
br.com.validatordoc
├── identity          RG, CIN e CNH em imagem
├── cnhpdf            CNH em PDF
├── coren             carteira e registro profissional
├── criminalrecord    antecedentes criminais
├── coursecertificate certificados de cursos
├── residenceproof    armazenamento sem leitura
├── ingestion         upload, MIME, hash e antivírus
├── storage           abstração S3/MinIO
├── pdf               parsing e renderização segura
├── signature         PAdES, X.509 e confiança
├── qrcode            leitura e política de URLs
├── issuer            conectores externos
├── session           ciclo de vida da sessão
├── decision          regras e estados finais
├── review            fila e decisão humana
├── webhook           notificações assinadas
├── audit             trilha de auditoria
├── tenant            organização e isolamento
└── shared            tipos técnicos compartilhados
```

Cada módulo deverá expor serviços de aplicação e contratos explícitos. Entidades internas não deverão ser reutilizadas diretamente por outros módulos.

## 4. Portas principais

```java
public interface DocumentProcessor {
    boolean supports(DocumentType type, MediaType mediaType);
    ProcessingResult process(ProcessingContext context);
}

public interface ObjectStorage {
    StoredObject store(StoreRequest request);
    InputStream read(String objectKey);
    void delete(String objectKey);
    URI createTemporaryViewUrl(String objectKey, Duration ttl);
}

public interface IdentityProvider {
    IdentityExtractionResult analyze(IdentityRequest request);
}

public interface IssuerConnector {
    boolean supports(Issuer issuer);
    IssuerCheckResult validate(IssuerCheckRequest request);
}
```

## 5. Tecnologias

| Área | Escolha inicial |
|---|---|
| Backend | Java 21 + Spring Boot |
| Frontend | Next.js + TypeScript |
| Banco | PostgreSQL |
| Migração | Flyway |
| Fila | RabbitMQ |
| Cache/locks | Redis |
| Arquivos | S3 ou MinIO |
| PDF | Apache PDFBox |
| Detecção MIME | Apache Tika |
| Assinatura | EU DSS + Bouncy Castle |
| QR Code | ZXing |
| Malware | ClamAV |
| Telemetria | OpenTelemetry |
| Métricas | Prometheus |
| Dashboards | Grafana |
| Testes | JUnit 5, Testcontainers, WireMock |

## 6. Fluxo de upload

1. Cliente cria sessão.
2. API devolve slots e limites.
3. Cliente envia para API ou URL assinada.
4. Ingestão valida MIME, estrutura e tamanho.
5. Arquivo recebe hash e estado `SCANNING`.
6. ClamAV analisa o conteúdo.
7. Arquivo aprovado passa para `AVAILABLE`.
8. Quando todos os slots estão disponíveis, a sessão pode ser enfileirada.

## 7. Fluxo de processamento

1. API grava `QUEUED` e publica `DocumentProcessingRequested`.
2. Worker adquire lock idempotente.
3. Carrega configuração e arquivos.
4. Executa o processador.
5. Persiste campos, checks, evidências e versão.
6. Executa o motor de decisão.
7. Define `VALID`, `INVALID`, `REVIEW_REQUIRED` ou outro estado.
8. Publica conclusão e agenda webhook.

## 8. Segurança de rede

- API pública atrás de WAF/reverse proxy.
- Banco, fila, Redis, storage e ClamAV em rede privada.
- Egress de workers restrito a fornecedores e domínios autorizados.
- Conectores que consultam URLs deverão usar proteção contra SSRF, DNS rebinding e redirecionamentos não permitidos.

## 9. Estratégia para SDK documental

O fornecedor deverá possuir integração Web ou API self-hosted/servidor. O domínio não dependerá dos DTOs do SDK.

```text
IdentityDocumentProcessor
        |
        v
IdentityProvider (porta)
        |
        +-- BlinkIdAdapter
        +-- AlternativeProviderAdapter
        `-- MockIdentityProvider
```

A contratação dependerá de prova de conceito com documentos brasileiros autorizados.

## 10. Implantação inicial

```text
container web
container api
container worker
container postgres
container rabbitmq
container redis
container minio
container clamav
container otel-collector
```

Em produção, serviços gerenciados podem substituir componentes locais. Kubernetes somente deverá ser adotado quando volume, disponibilidade e equipe justificarem.

## 11. Consistência e idempotência

- Banco é a fonte de verdade dos estados.
- Publicação de eventos deve usar transactional outbox.
- Mensagens podem ser entregues mais de uma vez.
- Processadores deverão reconhecer tentativa já concluída.
- Webhooks usarão ID único e registro de entrega.

## 12. Evolução

Um módulo só deverá virar microsserviço quando existir necessidade mensurável de escala, segurança, ciclo de implantação ou propriedade de equipe. Possíveis candidatos futuros: ingestão de arquivos, processamento documental e webhooks.