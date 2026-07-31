# 06 — Processamento por documento

## 1. Regra central

Cada documento possui uma estratégia explícita. **Ler dados não significa validar autenticidade.** O resultado deverá separar extração, verificações e decisão.

## 2. Pipeline comum

1. Receber arquivo em slot conhecido.
2. Validar tamanho, assinatura binária, MIME e estrutura.
3. Calcular SHA-256.
4. Executar antivírus.
5. Remover metadados de imagens e gerar cópia sanitizada.
6. Preservar PDF original quando assinatura precisar ser verificada.
7. Selecionar processador.
8. Registrar tentativa e versões.
9. Extrair dados.
10. Executar checks independentes.
11. Aplicar regras de decisão.
12. Encaminhar para revisão quando necessário.

## 3. RG e CIN

**Entrada:** `FRONT` e `BACK`, JPEG/PNG.

**Processamento:**

- qualidade de imagem;
- identificação do documento e lados;
- extração por SDK homologado;
- CPF, nome, número, nascimento, emissão, validade, órgão e UF;
- comparação entre lados;
- validação matemática do CPF e coerência de datas.

**Não comprova sozinho:** autenticidade oficial, presença física ou identidade do portador.

## 4. CNH em imagem

**Entrada:** frente e verso conforme modelo aceito.

Além do pipeline de identidade, deverá registrar categoria, validade, número de registro e QR Code quando disponível. O suporte a cada modelo deverá ser comprovado em benchmark.

## 5. CNH em PDF

**Entrada:** PDF original exportado por fonte legítima.

Ordem:

1. validar estrutura sem modificar o original;
2. detectar criptografia, anexos, scripts e conteúdo ativo;
3. detectar assinaturas PAdES;
4. verificar integridade e alterações posteriores;
5. construir e validar cadeia X.509 segundo política;
6. verificar carimbo de tempo e revogação quando configurado;
7. extrair texto nativo;
8. renderizar páginas em sandbox se necessário;
9. detectar QR Code;
10. comparar dados.

Resultado deverá diferenciar `signaturePresent`, `cryptographicallyValid`, `trustedChain` e `documentIntegrityValid`.

## 6. Coren

**Entrada:** JPG/PNG.

Campos: nome, número, UF, categoria, emissão/validade quando existentes e QR Code.

Validação:

- normalização do registro;
- identificação do Conselho Regional;
- consulta por conector oficial/autorizado;
- comparação de nome, categoria e situação.

Sem API ou canal automatizável: revisão manual. CAPTCHA nunca será contornado.

## 7. Antecedentes criminais

**Entrada:** PDF.

Campos: emissor, titular, documento, número da certidão, código de controle, emissão, validade e conteúdo declarado.

Evidências aceitas:

- assinatura digital válida;
- QR Code confirmado em domínio oficial;
- código confirmado em mecanismo oficial;
- consulta por conector autorizado.

A frase “nada consta” isolada não é validação. O sistema deverá registrar o emissor e o alcance da certidão, sem produzir interpretação jurídica.

## 8. Certificados de cursos

**Entrada:** JPG/PNG/PDF.

Campos possíveis: participante, curso, instituição, carga horária, conclusão, emissão, código e responsáveis.

Estratégias:

- assinatura digital;
- QR Code em domínio aprovado;
- código em portal do emissor;
- conector específico;
- revisão humana.

Sem mecanismo: `NOT_VERIFIABLE`. Uma assinatura válida comprova integridade/autoria criptográfica, não necessariamente reconhecimento regulatório do curso.

## 9. Comprovante de residência

**Entrada:** JPG/PNG/PDF.

Estratégia `STORAGE_ONLY`:

- segurança do arquivo;
- hash;
- armazenamento privado;
- auditoria;
- retenção.

Não executar OCR, extração de endereço ou validação do emissor. Resultado: `STORED`, nunca `VALID_ADDRESS`.

## 10. Checks padronizados

```text
FILE_STRUCTURE
MALWARE_SCAN
IMAGE_QUALITY
DOCUMENT_CLASSIFICATION
FIELD_EXTRACTION
CPF_SYNTAX
DATE_COHERENCE
FRONT_BACK_MATCH
PDF_SIGNATURE
PDF_INTEGRITY
CERTIFICATE_TRUST
QR_CODE
APPROVED_DOMAIN
ISSUER_LOOKUP
REGISTRATION_STATUS
EXPIRATION
HUMAN_REVIEW
```

Estados: `PASSED`, `FAILED`, `UNCERTAIN`, `NOT_APPLICABLE`, `UNAVAILABLE`, `NOT_PERFORMED`.

## 11. Motor de decisão

Regras serão versionadas. Exemplo:

- check obrigatório falhou: `INVALID` ou `REVIEW_REQUIRED` conforme natureza;
- fornecedor indisponível: `REVIEW_REQUIRED`, não `INVALID`;
- ausência de autenticação: `NOT_VERIFIABLE`;
- arquivo inseguro: `INVALID` e quarentena;
- comprovante de residência seguro: estado operacional `STORED`.

## 12. Revisão humana

O revisor deverá receber motivos, campos, checks, imagens sanitizadas e links temporários. Correções e decisões precisam preservar o valor original e criar nova revisão auditável.