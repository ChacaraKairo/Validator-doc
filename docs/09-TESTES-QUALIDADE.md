# 09 — Testes e critérios de aceite

## 1. Estratégia

A qualidade deverá ser verificada em quatro dimensões: software, segurança, integração e desempenho documental. Uma demonstração visual não é suficiente para liberar aprovação automática.

## 2. Pirâmide de testes

### Unitários

- transições de estado;
- validação de CPF e datas;
- regras de slots;
- motor de decisão;
- normalização de campos;
- política de retenção;
- assinatura e verificação de webhooks;
- política de URLs e domínios.

### Integração

Usar Testcontainers para PostgreSQL, RabbitMQ, Redis, MinIO e ClamAV quando possível.

Cobrir:

- criação de sessão e upload;
- outbox e fila;
- persistência de tentativas;
- storage e exclusão;
- isolamento entre organizações;
- migrações Flyway;
- reprocessamento idempotente.

### Contrato

- OpenAPI validada em CI;
- compatibilidade entre site e API;
- adapters de fornecedores com fixtures sanitizadas;
- WireMock para emissores e falhas externas;
- webhooks com assinatura, replay e retentativa.

### Ponta a ponta

- fluxo completo por tipo documental;
- nova captura;
- falha de antivírus;
- revisão humana;
- visualização temporária;
- expiração e exclusão.

## 3. Segurança

Testar obrigatoriamente:

- IDOR e acesso cruzado entre organizações;
- bypass de autorização;
- MIME/extensão divergentes;
- arquivo poliglota;
- path traversal;
- PDF bomb e imagens gigantes;
- arquivo protegido por senha;
- conteúdo ativo em PDF;
- SSRF e DNS rebinding por QR Code;
- replay de webhook;
- race conditions de processamento;
- exposição de dados em logs e erros;
- enumeração por hash ou ID.

Ferramentas possíveis: OWASP ZAP, Semgrep, CodeQL, Trivy, Dependabot/Renovate e scanners de segredo.

## 4. Dataset documental

O benchmark deve utilizar documentos autorizados, com controle de acesso e separação entre:

- desenvolvimento;
- validação;
- teste final nunca usado para ajuste.

Cobertura mínima:

- modelos e estados diferentes;
- documentos novos e antigos;
- JPG/PNG/PDF;
- câmeras e resoluções variadas;
- reflexo, corte, rotação e desfoque;
- nomes longos e acentos;
- documentos válidos, expirados, divergentes e não suportados.

Dados sintéticos devem ser usados para testes comuns. Documentos reais só em ambiente controlado.

## 5. Métricas documentais

Por campo:

- exact match;
- character error rate;
- precisão, revocação e F1;
- taxa de campo ausente;
- taxa de correção humana.

Por decisão:

- falso válido;
- falso inválido;
- taxa de revisão;
- taxa de nova captura;
- não verificável;
- tempo até decisão.

A métrica prioritária é minimizar aprovação incorreta, não maximizar aprovação automática.

## 6. Metas iniciais para piloto

Metas serão confirmadas pelo negócio e benchmark. Referência inicial:

- zero aprovação automática conhecida com CPF inválido;
- 100% dos arquivos inseguros bloqueados nos casos de teste;
- 100% dos acessos cruzados negados;
- 100% das decisões com versões e motivos;
- 100% das visualizações auditadas;
- p95 de endpoints de metadados abaixo de 500 ms;
- p95 de processamento sem revisão abaixo de 2 minutos, sujeito a terceiros.

Nenhuma meta de extração deverá ser fixada antes da prova com o fornecedor.

## 7. Critérios de aceite por documento

### RG/CIN/CNH imagem

- formatos e lados validados;
- qualidade retornada;
- campos normalizados;
- divergências registradas;
- resposta do fornecedor encapsulada;
- caso incerto encaminhado à revisão.

### CNH PDF

- original preservado;
- assinatura e integridade separadas;
- QR Code tratado como entrada não confiável;
- alterações posteriores registradas;
- cadeia de confiança configurável.

### Coren

- número, UF e categoria modelados;
- ausência de consulta não gera `VALID`;
- conector não contorna CAPTCHA;
- revisão manual disponível.

### Antecedentes

- emissor identificado;
- texto isolado não aprova;
- validade temporal calculada;
- evidência oficial registrada quando disponível.

### Certificado

- mecanismo de autenticidade registrado;
- domínio e redirecionamento controlados;
- sem mecanismo resulta em `NOT_VERIFIABLE`.

### Comprovante de residência

- nenhuma leitura obrigatória;
- arquivo seguro armazenado;
- resultado não afirma validação do endereço;
- acesso privado e auditado.

## 8. Carga e resiliência

Cenários:

- upload simultâneo;
- fila crescente;
- fornecedor lento ou indisponível;
- RabbitMQ reiniciado;
- worker encerrado durante processamento;
- storage temporariamente indisponível;
- webhook retornando 429/500;
- job de exclusão parcial.

## 9. CI

Pipeline mínimo:

1. formatação e lint;
2. compilação;
3. unitários;
4. testes de arquitetura;
5. integração com containers;
6. validação OpenAPI;
7. SAST/SCA/segredos;
8. build de imagem;
9. scan da imagem;
10. publicação somente após aprovação.

## 10. Evidências de release

Cada release deverá guardar versão, commit, migrações, SBOM, resultados dos testes, vulnerabilidades aceitas, mudanças de regras e plano de rollback.