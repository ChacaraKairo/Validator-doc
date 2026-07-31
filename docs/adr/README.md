# Architecture Decision Records — ADR

Esta pasta registra decisões arquiteturais relevantes, seu contexto, alternativas, consequências e estado. ADRs aprovados não devem ser reescritos para esconder decisões antigas; uma nova decisão deverá substituir a anterior.

## Modelo

```markdown
# ADR-NNN — Título

- Estado: Proposto | Aceito | Substituído | Rejeitado
- Data: AAAA-MM-DD
- Responsáveis: nomes ou papéis

## Contexto

Problema, restrições e forças relevantes.

## Decisão

Decisão objetiva.

## Alternativas consideradas

Alternativas e motivos para não escolhê-las.

## Consequências

Benefícios, custos, riscos e trabalho futuro.

## Evidências

Benchmarks, documentos, provas de conceito e links internos.
```

## Decisões iniciais propostas

### ADR-001 — API independente do site

**Estado:** Aceito.

A API será um produto independente. O site será apenas o primeiro cliente. Isso permite integrações futuras e impede que regras documentais fiquem no frontend.

### ADR-002 — Java 21 e Spring Boot na API

**Estado:** Proposto até a prova técnica.

Escolha motivada pelo ecossistema empresarial, PDF, X.509, PAdES, segurança e processamento assíncrono. A decisão deverá ser confirmada com uma prova de assinatura digital e upload seguro.

### ADR-003 — Monólito modular no MVP

**Estado:** Aceito.

O projeto começará com módulos internos e processos separados de API/worker, evitando complexidade de microsserviços. Extração futura dependerá de necessidade mensurável.

### ADR-004 — Arquivos fora do PostgreSQL

**Estado:** Aceito.

Binários serão armazenados em S3/MinIO privado. PostgreSQL guardará metadados, estados, resultados e auditoria.

### ADR-005 — Processamento assíncrono

**Estado:** Aceito.

Validações serão executadas por fila. Upload e criação de sessão não esperarão fornecedores externos.

### ADR-006 — Fornecedores atrás de portas internas

**Estado:** Aceito.

SDKs e APIs externas serão encapsulados por interfaces próprias. DTOs de fornecedores não serão contratos públicos.

### ADR-007 — Comprovante de residência como STORAGE_ONLY

**Estado:** Aceito.

O comprovante será armazenado com segurança sem OCR obrigatório. O sistema não afirmará validação do endereço.

### ADR-008 — Sem aprovação pela leitura isolada

**Estado:** Aceito.

OCR ou texto extraído não comprovam autenticidade. Aprovação depende das evidências configuradas para cada tipo.

### ADR-009 — Revisão humana como estado normal

**Estado:** Aceito.

Casos incertos, fontes indisponíveis e documentos não verificáveis serão encaminhados para revisão, sem converter incerteza em aprovação ou fraude.

### ADR-010 — Não contornar CAPTCHA

**Estado:** Aceito.

Conectores usarão APIs, mecanismos oficiais ou revisão manual. O sistema não contornará controles de acesso de emissores.

## Próximos ADRs necessários

- fornecedor documental selecionado;
- política de trust store e revogação;
- provedor de identidade/OIDC;
- S3 gerenciado versus MinIO;
- RabbitMQ gerenciado versus próprio;
- estratégia de criptografia de campos;
- política de retenção por documento;
- regra inicial de aprovação automática;
- primeiro emissor de antecedentes;
- regionais do Coren prioritários.