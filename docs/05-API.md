# 05 — Contratos e endpoints da API

## 1. Convenções

- Base: `/api/v1`.
- JSON em UTF-8.
- Datas em ISO 8601 UTC.
- IDs opacos, preferencialmente UUIDv7 ou ULID.
- Autenticação por bearer token.
- `Idempotency-Key` em operações de criação.
- `X-Correlation-Id` aceito e devolvido.
- Erros no padrão Problem Details.

## 2. Tipos documentais

```text
RG
CIN
CNH
COREN_CARD
CRIMINAL_RECORD_CERTIFICATE
COURSE_CERTIFICATE
RESIDENCE_PROOF
```

## 3. Criar sessão

`POST /api/v1/document-sessions`

```json
{
  "personReference": "employee-123",
  "documentType": "RG",
  "purpose": "EMPLOYMENT_ONBOARDING",
  "metadata": {
    "processReference": "hiring-456"
  }
}
```

```json
{
  "id": "dvs_01K...",
  "status": "WAITING_UPLOAD",
  "documentType": "RG",
  "requiredFiles": [
    {
      "slot": "FRONT",
      "required": true,
      "acceptedMediaTypes": ["image/jpeg", "image/png"],
      "maxSizeBytes": 12582912
    },
    {
      "slot": "BACK",
      "required": true,
      "acceptedMediaTypes": ["image/jpeg", "image/png"],
      "maxSizeBytes": 12582912
    }
  ],
  "expiresAt": "2026-08-01T12:00:00Z"
}
```

## 4. Upload pela API

`POST /api/v1/document-sessions/{sessionId}/files`

`multipart/form-data`:

- `slot`: `FRONT`, `BACK` ou `DOCUMENT`.
- `file`: conteúdo.

```json
{
  "fileId": "file_01K...",
  "slot": "FRONT",
  "status": "SCANNING",
  "detectedMediaType": "image/jpeg",
  "sizeBytes": 1200345
}
```

## 5. Upload direto

`POST /api/v1/document-sessions/{sessionId}/upload-authorizations`

```json
{
  "slot": "DOCUMENT",
  "declaredMediaType": "application/pdf",
  "sizeBytes": 800000
}
```

Resposta contém URL assinada curta, headers obrigatórios e `uploadId`. Após o envio:

`POST /api/v1/document-sessions/{sessionId}/upload-authorizations/{uploadId}/complete`

A API deverá verificar objeto, tamanho, hash e conteúdo antes de aceitá-lo.

## 6. Iniciar processamento

`POST /api/v1/document-sessions/{sessionId}/process`

```json
{
  "status": "QUEUED",
  "processingAttemptId": "attempt_01K..."
}
```

Erros esperados:

- `409`: slots obrigatórios ausentes ou sessão já finalizada.
- `422`: arquivo rejeitado.
- `429`: limite da organização.

## 7. Consultar sessão

`GET /api/v1/document-sessions/{sessionId}`

```json
{
  "id": "dvs_01K...",
  "status": "PROCESSING",
  "documentType": "CNH",
  "files": [
    {"slot": "DOCUMENT", "status": "AVAILABLE"}
  ],
  "progress": {
    "stage": "SIGNATURE_VALIDATION",
    "percentage": 55
  }
}
```

Percentual é informativo e não deve ser usado como garantia temporal.

## 8. Consultar resultado

`GET /api/v1/document-sessions/{sessionId}/result`

```json
{
  "contractVersion": "1.0",
  "sessionId": "dvs_01K...",
  "documentType": "COREN_CARD",
  "status": "REVIEW_REQUIRED",
  "extractedFields": {
    "fullName": {
      "value": "NOME DO PROFISSIONAL",
      "normalizedValue": "NOME DO PROFISSIONAL",
      "confidence": 0.97,
      "validationStatus": "VALID"
    },
    "registrationNumber": {
      "value": "123456",
      "confidence": 0.95,
      "validationStatus": "UNCERTAIN"
    }
  },
  "checks": [
    {
      "type": "IMAGE_QUALITY",
      "status": "PASSED",
      "performedAt": "2026-07-31T20:00:00Z"
    },
    {
      "type": "OFFICIAL_REGISTRATION",
      "status": "UNAVAILABLE",
      "reasonCode": "CONNECTOR_NOT_CONFIGURED"
    }
  ],
  "decision": {
    "status": "REVIEW_REQUIRED",
    "reasonCodes": ["OFFICIAL_CHECK_UNAVAILABLE"],
    "requiresHumanReview": true
  },
  "versions": {
    "processor": "coren-processor:1.0.0",
    "rules": "employment-default:1"
  }
}
```

## 9. Retry e reprocessamento

`POST /api/v1/document-sessions/{sessionId}/retry`

Cria nova tentativa técnica para falha recuperável.

`POST /api/v1/document-sessions/{sessionId}/reprocess`

Permitido somente a operadores autorizados. Pode receber versão de regras ou provedor. Histórico anterior permanece imutável.

## 10. Revisão

`GET /api/v1/reviews?status=PENDING&documentType=COREN_CARD`

`GET /api/v1/reviews/{reviewId}`

`POST /api/v1/reviews/{reviewId}/decision`

```json
{
  "decision": "VALID",
  "reasonCode": "OFFICIAL_SOURCE_CONFIRMED_MANUALLY",
  "notes": "Registro conferido em canal oficial.",
  "correctedFields": {}
}
```

## 11. Visualização temporária

`POST /api/v1/documents/{documentId}/view-sessions`

```json
{
  "url": "https://storage.example/...",
  "expiresAt": "2026-07-31T20:17:00Z"
}
```

A URL deverá expirar rapidamente, ser vinculada ao arquivo e produzir evento de auditoria.

## 12. Exclusão

`DELETE /api/v1/document-sessions/{sessionId}`

A resposta poderá ser `202 Accepted` quando a exclusão for assíncrona. Restrições legais ou retenções deverão ser informadas por código estável.

## 13. Webhook

Evento `document.validation.completed`:

```json
{
  "eventId": "evt_01K...",
  "eventType": "document.validation.completed",
  "occurredAt": "2026-07-31T20:00:00Z",
  "organizationId": "org_01K...",
  "data": {
    "sessionId": "dvs_01K...",
    "documentType": "RG",
    "status": "VALID"
  }
}
```

Headers:

- `X-Validator-Event-Id`
- `X-Validator-Timestamp`
- `X-Validator-Signature`

Assinatura: HMAC sobre timestamp e corpo bruto. O receptor deverá validar janela temporal e event ID.

## 14. Erros

```json
{
  "type": "https://docs.example/problems/invalid-document-file",
  "title": "Arquivo documental inválido",
  "status": 422,
  "code": "FILE_STRUCTURE_INVALID",
  "detail": "O arquivo não corresponde a um PNG válido.",
  "correlationId": "corr_01K..."
}
```

Nunca retornar stack trace, caminho interno, resposta bruta de fornecedor ou dado pessoal desnecessário.

## 15. Códigos de estado da sessão

```text
CREATED
WAITING_UPLOAD
UPLOADED
QUEUED
PROCESSING
VALID
INVALID
PARTIALLY_VALIDATED
NOT_VERIFIABLE
REVIEW_REQUIRED
UNSUPPORTED
FAILED
CANCELLED
EXPIRED
DELETED
```

## 16. Paginação

Listagens usarão cursor opaco:

`GET /api/v1/reviews?limit=50&cursor=...`

A resposta deverá conter `items` e `nextCursor`.