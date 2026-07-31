# Validator Doc API

Fundação da API independente de validação documental.

## Requisitos

- Java 21
- Maven 3.9+
- Docker com Compose

## Executar localmente

Na raiz do repositório:

```bash
docker compose up -d postgres
cd api
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

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
    "documentType": "CNH"
  }'
```

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

- projeto Spring Boot;
- domínio inicial de sessões documentais;
- persistência PostgreSQL;
- migrations Flyway;
- criação e consulta de sessões;
- validação das requisições;
- erros REST padronizados;
- Actuator;
- ambiente PostgreSQL via Docker Compose.

Próximos passos:

- upload de arquivos por slots;
- armazenamento S3/MinIO;
- hash SHA-256 e validação de MIME;
- fila de processamento;
- processadores por tipo documental;
- autenticação e isolamento por organização.
