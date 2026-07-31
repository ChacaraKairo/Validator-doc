# 07 — Modelo de dados

## 1. Diretrizes

- PostgreSQL armazena metadados, estados, resultados e auditoria.
- Binários ficam em S3/MinIO, nunca em colunas `bytea` por padrão.
- Todas as entidades de negócio possuem `organization_id`.
- Resultados e decisões são versionados e não sobrescritos silenciosamente.
- Dados pessoais devem ser minimizados e, quando necessário, criptografados em nível de aplicação.

## 2. Entidades principais

### organizations

- `id`
- `name`
- `status`
- `created_at`, `updated_at`

### organization_settings

- limites de upload;
- tipos permitidos;
- retenção por finalidade;
- regra de decisão ativa;
- integrações habilitadas.

### api_clients / users / roles

Identidades, credenciais referenciadas, papéis e escopos. Segredos não são armazenados em texto puro.

### document_sessions

- `id`
- `organization_id`
- `person_reference`
- `process_reference`
- `document_type`
- `purpose`
- `status`
- `expires_at`
- `created_by`
- timestamps
- `version` para optimistic locking

### document_files

- `id`
- `session_id`
- `slot`
- `original_filename`
- `object_key`
- `sanitized_object_key`
- `declared_media_type`
- `detected_media_type`
- `size_bytes`
- `sha256`
- `status`
- `malware_scan_status`
- `page_count`, `width`, `height`
- `retention_until`
- timestamps

### processing_attempts

- `id`
- `session_id`
- `attempt_number`
- `processor_name`, `processor_version`
- `ruleset_name`, `ruleset_version`
- `provider_name`, `provider_version`
- `status`
- `started_at`, `finished_at`
- `error_code`

### extracted_fields

- `id`
- `attempt_id`
- `field_name`
- `raw_value_encrypted`
- `normalized_value_encrypted`
- `masked_value`
- `confidence`
- `source_slot`
- `validation_status`
- `position_json`

Valores sensíveis podem ser mantidos apenas quando necessários. Campos indexáveis deverão usar estratégia de tokenização ou hash adequado à finalidade.

### validation_checks

- `id`
- `attempt_id`
- `check_type`
- `status`
- `reason_code`
- `source_type`
- `source_reference`
- `evidence_json`
- `performed_at`
- `expires_at`

### validation_decisions

- `id`
- `attempt_id`
- `status`
- `reason_codes`
- `requires_review`
- `decision_source` (`RULE_ENGINE`, `HUMAN`)
- `ruleset_version`
- `created_at`

### reviews

- `id`
- `session_id`
- `status`
- `priority`
- `reason_codes`
- `assigned_to`
- `due_at`
- timestamps

### review_decisions

- `id`
- `review_id`
- `reviewer_id`
- `decision`
- `reason_code`
- `notes_encrypted`
- `corrected_fields_json_encrypted`
- `created_at`

### issuer_connectors

Configuração lógica de emissores, sem segredos. Credenciais são referências ao secret manager.

### webhook_endpoints / webhook_deliveries

Endpoints, eventos assinados, tentativas, respostas e próxima entrega.

### audit_events

- organização;
- ator e tipo do ator;
- ação;
- recurso e ID;
- horário;
- correlation ID;
- IP tratado conforme política;
- resultado;
- metadados sem conteúdo documental.

### outbox_events

Eventos transacionais ainda não publicados.

### retention_jobs

Controle de exclusão, tentativas, objetos removidos e bloqueios legais.

## 3. Relações

```text
organization 1---N document_session
session      1---N document_file
session      1---N processing_attempt
attempt      1---N extracted_field
attempt      1---N validation_check
attempt      1---N validation_decision
session      0---N review
review       0---N review_decision
```

## 4. Índices

- `(organization_id, id)` em recursos consultáveis.
- `(organization_id, person_reference)`.
- `(organization_id, status, created_at)` em sessões e revisões.
- `(session_id, slot)` único para slots não repetíveis.
- `(session_id, attempt_number)` único.
- `(sha256, organization_id)` apenas para controles internos autorizados.
- `outbox_events(published_at, created_at)`.

## 5. Estados de arquivo

```text
RECEIVED
UPLOADING
SCANNING
AVAILABLE
QUARANTINED
REJECTED
DELETION_PENDING
DELETED
```

## 6. Integridade

- FK e constraints para enums críticos.
- Transições de estado validadas pela aplicação.
- `NOT NULL` para identidade organizacional.
- Exclusão lógica apenas onde houver necessidade de auditoria; binários devem ser fisicamente removidos ao expirar.

## 7. Dados específicos

Campos específicos devem ser representados no contrato do resultado, mas persistidos no modelo genérico de `extracted_fields` e em projeções quando houver necessidade de consulta. Evitar criar uma tabela para cada variação de documento antes de conhecer os requisitos reais.

## 8. Armazenamento de objetos

```text
organizations/{organizationId}/
sessions/{sessionId}/
files/{fileId}/original
files/{fileId}/sanitized
artifacts/{attemptId}/...
```

Nunca usar nome, CPF, Coren ou número documental na chave.

## 9. Retenção

Cada arquivo e artefato deverá possuir data de retenção efetiva. A exclusão deve remover original, sanitizado, miniaturas, artefatos OCR e caches. Metadados mínimos de auditoria podem ter prazo diferente, conforme política.