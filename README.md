# Validator-doc

Plataforma web e API independente para recebimento, processamento, validação e armazenamento seguro de documentos.

## Visão geral

O Validator-doc será composto inicialmente por uma **API de validação documental**. Um site web será criado como primeiro cliente dessa API, mas a API deverá permanecer desacoplada para permitir futuras integrações com sistemas de RH, ERPs, aplicativos e parceiros.

## Documentos suportados

| Documento | Formatos | Operação |
|---|---|---|
| RG / CIN | JPG, JPEG, PNG | Captura, extração e validação |
| CNH física | JPG, JPEG, PNG | Captura, extração e validação |
| CNH digital | PDF | Assinatura, integridade, QR Code e extração |
| Carteira Coren | JPG, JPEG, PNG | Extração, QR Code e consulta cadastral quando disponível |
| Antecedentes criminais | PDF | Extração e validação por assinatura, código, QR Code ou emissor |
| Certificados de cursos | JPG, JPEG, PNG, PDF | Extração e verificação conforme o mecanismo do emissor |
| Comprovante de residência | JPG, JPEG, PNG, PDF | Armazenamento seguro, sem leitura obrigatória |

## Arquitetura proposta

- **Site:** Next.js, React e TypeScript.
- **API:** Java 21 e Spring Boot.
- **Banco:** PostgreSQL.
- **Arquivos:** S3 ou MinIO com criptografia e acesso privado.
- **Fila:** RabbitMQ.
- **Cache:** Redis.
- **Identidade:** SDK documental Web ou API self-hosted.
- **PDF:** Apache PDFBox e Apache Tika.
- **Assinaturas:** EU DSS e Bouncy Castle.
- **QR Code:** ZXing.
- **Antivírus:** ClamAV.
- **Observabilidade:** OpenTelemetry, Prometheus e Grafana.

A primeira versão deve ser um **monólito modular**, evitando microsserviços prematuros.

## Princípios

1. Extração não significa autenticidade.
2. Nenhum documento incerto deve ser aprovado silenciosamente.
3. Dados pessoais não devem aparecer em logs.
4. Arquivos devem ser privados, criptografados e sujeitos a retenção.
5. Integrações oficiais devem respeitar termos de uso, APIs e controles dos emissores.
6. O sistema não deve contornar CAPTCHA ou mecanismos de proteção.
7. Toda decisão deve produzir evidências e trilha de auditoria.

## Documentação

1. [Visão, objetivos e escopo](docs/01-VISAO-ESCOPO.md)
2. [Requisitos funcionais](docs/02-REQUISITOS-FUNCIONAIS.md)
3. [Requisitos não funcionais](docs/03-REQUISITOS-NAO-FUNCIONAIS.md)
4. [Arquitetura e tecnologias](docs/04-ARQUITETURA.md)
5. [Contratos e endpoints da API](docs/05-API.md)
6. [Processamento por documento](docs/06-PROCESSAMENTO-DOCUMENTAL.md)
7. [Modelo de dados](docs/07-MODELO-DE-DADOS.md)
8. [Segurança, privacidade e LGPD](docs/08-SEGURANCA-LGPD.md)
9. [Testes e critérios de aceite](docs/09-TESTES-QUALIDADE.md)
10. [Roadmap e backlog inicial](docs/10-ROADMAP.md)
11. [Operação e observabilidade](docs/11-OPERACAO.md)
12. [Decisões arquiteturais](docs/adr/README.md)

## Status

Projeto em fase de especificação e arquitetura. As tecnologias e fornecedores externos deverão ser confirmados por prova de conceito e benchmark antes da contratação e implementação em produção.

## Aviso

Este projeto processará dados pessoais e documentos sensíveis ao contexto de contratação. A implementação em produção exige análise jurídica, definição de base legal, política de retenção, contratos com operadores e processo formal de resposta a incidentes.