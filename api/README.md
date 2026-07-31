# Validator Doc API

API independente para ingestão, armazenamento e validação documental.

## Requisitos

- Java 21
- Maven 3.9+
- Docker com Compose

## Executar localmente

Na raiz do repositório:

```bash
docker compose up -d postgres minio
cd api
mvn spring-boot:run
```

Serviços locais:

- API: `http://localhost:8080`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`
- usuário MinIO: `minioadmin`
- senha MinIO: `minioadmin`

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Criar uma sessão documental

```bash
curl -X POST http://localhost:8080/api/v1/document-sessions \
  -H 'Content-Type: application/json' \
  -d '{
    "externalPersonId": "person-123",
    "documentType": "RESIDENCE_PROOF"
  }'
```

A resposta informa os slots permitidos e as combinações que concluem o upload.

## Enviar um arquivo

```bash
curl -X POST 'http://localhost:8080/api/v1/document-sessions/{sessionId}/files?slot=DOCUMENT' \
  -F 'file=@/caminho/comprovante.pdf'
```

Formatos aceitos globalmente:

- `image/jpeg`
- `image/png`
- `application/pdf`

O formato permitido depende do tipo documental e do slot.

### Slots por documento

- RG e CIN: `FRONT` + `BACK`, somente JPG/PNG;
- CNH: `FRONT` + `BACK` em JPG/PNG **ou** `DOCUMENT` em PDF;
- Coren: `FRONT` + `BACK`, somente JPG/PNG;
- antecedentes criminais: `DOCUMENT`, somente PDF;
- certificado: `DOCUMENT`, JPG/PNG/PDF;
- comprovante de residência: `DOCUMENT`, JPG/PNG/PDF.

## Segurança do upload

O pipeline:

1. limita o arquivo a 20 MB;
2. detecta o MIME real com Apache Tika;
3. confere a assinatura binária de JPG, PNG ou PDF;
4. calcula SHA-256;
5. bloqueia o mesmo hash na mesma sessão;
6. bloqueia o reenvio para um slot já preenchido;
7. armazena o objeto no MinIO;
8. grava metadados no PostgreSQL;
9. atualiza a sessão para `UPLOADED` quando os slots estiverem completos;
10. prepara o job de processamento.

Para `RESIDENCE_PROOF`, a estratégia `STORAGE_ONLY` é concluída sem OCR e a sessão passa para `VALID` após o armazenamento.

## Consultar uma sessão

```bash
curl http://localhost:8080/api/v1/document-sessions/{id}
```

## Testes

```bash
mvn test
```

## Estado atual

Implementado:

- Spring Boot e Java 21;
- sessões documentais;
- requisitos e slots por tipo;
- upload multipart;
- detecção de MIME e assinatura binária;
- SHA-256 e deduplicação;
- armazenamento MinIO;
- metadados PostgreSQL;
- migrations Flyway;
- preparação da fila;
- pipeline `STORAGE_ONLY`;
- erros REST padronizados;
- Actuator e Docker Compose.

Próximos passos:

- substituir a fila interna por RabbitMQ/outbox;
- antivírus com ClamAV;
- sanitização de imagens e PDFs;
- autenticação e isolamento por organização;
- processadores de identidade, CNH, Coren, antecedentes e certificados.
