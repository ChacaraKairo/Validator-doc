# Validator Doc API

API independente para ingestão, armazenamento e validação documental.

## Requisitos

- Java 21
- Maven 3.9+
- Docker com Compose

## Executar localmente

```bash
docker compose up -d postgres minio rabbitmq clamav
cd api
mvn spring-boot:run
```

Serviços locais:

- API: `http://localhost:8080`
- MinIO Console: `http://localhost:9001` (`minioadmin` / `minioadmin`)
- RabbitMQ Management: `http://localhost:15672` (`validator_doc` / `validator_doc`)
- ClamAV: `localhost:3310`

O ClamAV pode demorar na primeira inicialização enquanto baixa as definições de vírus. Confirme os serviços com:

```bash
docker compose ps
```

## Fluxo de upload

```bash
curl -X POST http://localhost:8080/api/v1/document-sessions \
  -H 'Content-Type: application/json' \
  -d '{"externalPersonId":"person-123","documentType":"RESIDENCE_PROOF"}'

curl -X POST \
  'http://localhost:8080/api/v1/document-sessions/{sessionId}/files?slot=DOCUMENT' \
  -F 'file=@/caminho/comprovante.pdf'
```

O pipeline executa, nesta ordem:

1. validação do slot e do tamanho;
2. detecção do MIME real com Apache Tika;
3. conferência da assinatura binária;
4. varredura por malware via protocolo `INSTREAM` do ClamAV;
5. sanitização do conteúdo;
6. cálculo SHA-256 da versão sanitizada;
7. deduplicação por sessão;
8. armazenamento privado no MinIO;
9. persistência dos metadados e do evento Outbox na mesma transação;
10. publicação confirmada no RabbitMQ;
11. consumo do job pelo processador correspondente.

## Sanitização

- JPG e PNG são decodificados e reencodados, removendo EXIF, GPS, thumbnails e metadados anexos.
- PDFs protegidos por senha são rejeitados.
- PDFs são carregados e salvos novamente com PDFBox, removendo ações de abertura, ações de catálogo e árvores de nomes que podem conter JavaScript ou anexos.
- O hash e o tamanho persistidos correspondem ao arquivo sanitizado efetivamente armazenado.

## Outbox e RabbitMQ

O upload não publica diretamente no broker. Ele grava uma linha em `processing_outbox` junto com a mudança da sessão para `UPLOADED`. Um publicador agendado:

- busca eventos pendentes;
- envia para `validator-doc.processing`;
- aguarda publisher confirm;
- marca `published_at` somente após `ACK` do RabbitMQ;
- mantém tentativas e último erro em caso de falha.

A fila durável é `validator-doc.processing.jobs`.

Para `RESIDENCE_PROOF`, o consumidor `STORAGE_ONLY` não executa OCR e muda a sessão para `VALID`.

## Testes

```bash
mvn test
```

Nos testes, ClamAV e listeners RabbitMQ ficam desabilitados para não exigir serviços externos.
