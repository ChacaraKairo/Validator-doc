# 10 — Roadmap e backlog inicial

## Fase 0 — Descoberta e governança

**Objetivo:** confirmar requisitos, fornecedores, base legal e documentos reais do processo.

Entregas:

- responsáveis de negócio, segurança e privacidade;
- matriz de documentos, formatos, campos e decisões;
- política de retenção inicial;
- critérios para revisão humana;
- análise de fornecedores de identidade;
- amostra autorizada e plano de benchmark;
- definição do primeiro emissor de antecedentes e regionais do Coren prioritários.

Aceite: escopo assinado e riscos conhecidos.

## Fase 1 — Fundação técnica

- monorepo ou repositórios definidos;
- Spring Boot, PostgreSQL e Flyway;
- autenticação e multi-tenancy;
- OpenAPI;
- Docker Compose;
- observabilidade básica;
- CI com testes e scanners;
- ADRs iniciais.

Aceite: API autenticada, health checks seguros, migração e pipeline funcionando.

## Fase 2 — Sessões, upload e storage

- criação de sessão;
- regras de slots;
- upload direto e pela API;
- Tika, decodificação e limites;
- SHA-256;
- ClamAV;
- S3/MinIO privado;
- remoção de EXIF;
- expiração de sessão;
- auditoria de upload.

Aceite: arquivos permitidos ficam disponíveis; ameaças e formatos inválidos são rejeitados/quarentenados.

## Fase 3 — Comprovante de residência

Primeiro tipo entregue por ser `STORAGE_ONLY`.

- endpoint específico por sessão;
- resultado `STORED`;
- URL temporária;
- auditoria de visualização;
- retenção e exclusão.

Aceite: arquivo é armazenado e excluído sem OCR ou afirmação de validade do endereço.

## Fase 4 — Infraestrutura de processamento

- RabbitMQ;
- transactional outbox;
- workers;
- idempotência;
- tentativas e DLQ;
- modelo de attempts, checks e decisions;
- webhooks assinados.

Aceite: processador de teste conclui de forma resiliente e não duplica decisão.

## Fase 5 — RG, CIN e CNH em imagem

- prova de conceito do SDK;
- adapter interno;
- campos normalizados;
- qualidade;
- CPF e datas;
- comparação de lados;
- benchmark;
- revisão humana.

Aceite: fornecedor homologado nos documentos priorizados e casos incertos não aprovados automaticamente.

## Fase 6 — PDF e CNH digital

- PDFBox/Tika;
- sandbox e limites;
- EU DSS/Bouncy Castle;
- cadeia de confiança;
- alterações incrementais;
- ZXing;
- política de URL/SSRF;
- resultado detalhado.

Aceite: PDFs assinados, inválidos, alterados, corrompidos e sem assinatura são distinguidos corretamente.

## Fase 7 — Coren

- extração de nome, número, UF e categoria;
- QR Code;
- cadastro de regionais;
- primeiro conector autorizado;
- fallback de revisão;
- suporte a múltiplas inscrições.

Aceite: registro consultável é comparado e ausência de consulta nunca vira aprovação automática.

## Fase 8 — Antecedentes criminais

- primeiro emissor priorizado;
- parser de layout;
- código/QR/assinatura;
- consulta autorizada;
- validade temporal;
- evidências e revisão.

Aceite: texto do PDF isolado não produz validação.

## Fase 9 — Certificados de cursos

- modelo genérico;
- assinatura e QR;
- allowlist de domínios;
- cadastro de emissores;
- conectores prioritários;
- `NOT_VERIFIABLE`.

Aceite: mecanismos são registrados e ausência de autenticação é apresentada claramente.

## Fase 10 — Site web

- autenticação;
- pessoas/processos;
- checklist documental;
- upload responsivo;
- progresso;
- resultados;
- revisão;
- acessibilidade;
- mensagens de nova captura.

Aceite: fluxo completo em desktop e mobile web.

## Fase 11 — Produção piloto

- threat model;
- pentest;
- teste de carga;
- backup/restauração;
- runbooks;
- treinamento;
- alertas;
- canary;
- revisão dos SLAs e métricas.

Aceite: piloto autorizado formalmente.

# Backlog inicial sugerido

## Épico A — Plataforma

- inicializar projeto Spring Boot;
- configurar arquitetura modular;
- criar migrations base;
- implementar organização e cliente de API;
- configurar OIDC;
- publicar OpenAPI;
- adicionar correlation ID.

## Épico B — Ingestão

- criar session state machine;
- implementar slots por documento;
- integrar storage;
- validar MIME/estrutura;
- integrar ClamAV;
- sanitizar imagens;
- implementar URLs temporárias.

## Épico C — Processamento

- configurar RabbitMQ e DLQ;
- criar outbox;
- criar `DocumentProcessor`;
- implementar worker idempotente;
- persistir attempts/checks/decisions;
- criar ruleset versionado.

## Épico D — Revisão

- criar fila;
- atribuição e prioridade;
- tela de evidências;
- decisão auditada;
- correção sem sobrescrever original.

## Épico E — Segurança

- RBAC;
- isolamento multi-tenant;
- secret manager;
- proteção SSRF;
- política de logs;
- job de retenção;
- auditoria imutável.

## Dependências externas antes de codificar validações

- escolher fornecedor documental;
- obter matriz de suporte Brasil;
- definir custo e licença;
- identificar APIs/termos de Coren e emissores;
- definir trust store e política de assinatura;
- aprovar política LGPD e retenção.