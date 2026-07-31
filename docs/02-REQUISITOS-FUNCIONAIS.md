# 02 — Requisitos funcionais

## Convenções

- `RF-xxx`: requisito funcional.
- Prioridade: `MUST`, `SHOULD` ou `COULD`.
- Os requisitos deverão ser rastreados para casos de teste e versões da API.

## 1. Organizações, usuários e acesso

### RF-001 — Organizações (`MUST`)
A API deverá separar dados, configurações e arquivos por organização.

### RF-002 — Autenticação (`MUST`)
A API deverá aceitar autenticação segura por OAuth 2.0/OpenID Connect ou credenciais de cliente para integrações servidor a servidor.

### RF-003 — Autorização (`MUST`)
Deverá existir controle por papéis, incluindo administrador, operador, revisor e cliente de integração.

### RF-004 — Escopo de acesso (`MUST`)
Um usuário ou cliente não poderá consultar documentos de outra organização.

## 2. Pessoas e solicitações

### RF-010 — Referência de pessoa (`MUST`)
Cada sessão deverá ser associada a uma referência externa de pessoa fornecida pelo sistema cliente. A API não deverá exigir que o cadastro completo da pessoa seja duplicado.

### RF-011 — Criação de sessão (`MUST`)
O cliente deverá criar uma sessão informando organização, referência da pessoa, tipo documental e finalidade.

### RF-012 — Regras de upload (`MUST`)
A resposta de criação deverá informar slots obrigatórios, formatos, quantidade, tamanho máximo e expiração.

### RF-013 — Expiração (`MUST`)
Sessões incompletas deverão expirar automaticamente.

### RF-014 — Idempotência (`MUST`)
Operações de criação e início de processamento deverão aceitar chave de idempotência.

## 3. Upload e ingestão

### RF-020 — Formatos permitidos (`MUST`)
A API deverá aceitar apenas os formatos configurados para o tipo documental.

### RF-021 — Validação real do arquivo (`MUST`)
A API deverá validar assinatura binária, MIME detectado, tamanho, capacidade de decodificação e estrutura.

### RF-022 — Hash (`MUST`)
Cada arquivo deverá receber SHA-256 calculado pelo servidor.

### RF-023 — Antivírus (`MUST`)
Todo arquivo deverá passar por análise de malware antes de ficar disponível.

### RF-024 — Metadados de imagem (`MUST`)
Imagens deverão ser reencodadas e ter EXIF removido, salvo quando houver justificativa documentada para preservar o original.

### RF-025 — PDF original (`MUST`)
PDFs submetidos para validação de assinatura deverão ter o original preservado sem alteração.

### RF-026 — Upload direto (`SHOULD`)
A API poderá emitir URL assinada para upload direto em armazenamento de objetos.

### RF-027 — Duplicidade (`SHOULD`)
A API deverá identificar arquivos iguais por hash dentro do escopo permitido, sem deduplicar entre organizações de forma que revele existência de dados.

## 4. Processamento

### RF-030 — Início explícito (`MUST`)
O processamento só poderá iniciar quando todos os slots obrigatórios estiverem disponíveis e aprovados pela ingestão.

### RF-031 — Processamento assíncrono (`MUST`)
A validação deverá ocorrer por fila, sem manter a requisição HTTP aberta.

### RF-032 — Estratégia por tipo (`MUST`)
Cada tipo documental deverá utilizar um processador próprio atrás de interface comum.

### RF-033 — Tentativas (`MUST`)
Falhas transitórias deverão utilizar tentativas limitadas e backoff. Falhas permanentes não deverão ser repetidas indefinidamente.

### RF-034 — Reprocessamento (`MUST`)
Operadores autorizados poderão reprocessar com nova versão de regras ou fornecedor, preservando histórico.

### RF-035 — Cancelamento (`SHOULD`)
Sessões ainda não concluídas poderão ser canceladas por usuário autorizado.

## 5. Identidade — RG, CIN e CNH em imagem

### RF-040 — Frente e verso (`MUST`)
O sistema deverá exigir os lados definidos pela configuração do documento.

### RF-041 — Qualidade (`MUST`)
O processador deverá avaliar corte, resolução, desfoque, reflexo e orientação conforme capacidades do fornecedor.

### RF-042 — Extração (`MUST`)
Deverá extrair, quando disponíveis: nome, CPF, número, nascimento, emissão, validade, órgão e UF.

### RF-043 — Correspondência (`MUST`)
Quando houver dados repetidos nos lados, o sistema deverá registrar concordância ou divergência.

### RF-044 — Validação sintática (`MUST`)
CPF, datas e valores enumerados deverão ser normalizados e validados independentemente do fornecedor.

### RF-045 — Resultado do fornecedor (`MUST`)
A API deverá transformar a resposta externa em contrato interno versionado.

## 6. CNH em PDF

### RF-050 — Estrutura (`MUST`)
O sistema deverá validar que o arquivo é um PDF legível, não corrompido e dentro dos limites.

### RF-051 — Assinatura (`MUST`)
Deverá detectar assinaturas, validar integridade criptográfica, cadeia, período de validade e estado de confiança conforme política configurada.

### RF-052 — Alterações posteriores (`MUST`)
O resultado deverá informar se houve alterações após a assinatura e se são permitidas.

### RF-053 — QR Code (`SHOULD`)
Deverá detectar e decodificar QR Codes, preservando conteúdo de forma segura.

### RF-054 — Comparação (`MUST`)
Campos extraídos do texto, assinatura e QR Code deverão ser comparados quando houver dados equivalentes.

## 7. Coren

### RF-060 — Dados profissionais (`MUST`)
Extrair nome, número, UF e categoria profissional quando legíveis.

### RF-061 — Múltiplas inscrições (`MUST`)
O modelo deverá aceitar mais de uma inscrição por pessoa.

### RF-062 — Consulta regional (`SHOULD`)
Quando existir integração autorizada, consultar o Conselho Regional correspondente.

### RF-063 — Sem integração (`MUST`)
Sem fonte automatizável, o resultado deverá ser `NOT_VERIFIABLE` ou `REVIEW_REQUIRED`.

### RF-064 — CAPTCHA (`MUST`)
O sistema não deverá contornar CAPTCHA. A consulta deverá ser manual ou por canal oficial.

## 8. Antecedentes criminais

### RF-070 — Identificação de emissor (`MUST`)
A API deverá identificar ou exigir o emissor configurado.

### RF-071 — Extração (`MUST`)
Extrair número, código de controle, nome, documento, emissão, validade e declaração apresentada.

### RF-072 — Verificação (`MUST`)
A validade deverá depender de assinatura, QR Code, código confirmado ou consulta oficial. Texto isolado não será suficiente.

### RF-073 — Validade temporal (`MUST`)
O resultado deverá indicar expiração conforme data do documento e regra do negócio.

## 9. Certificados de curso

### RF-080 — Extração (`MUST`)
Extrair instituição, participante, curso, carga horária, emissão, conclusão e identificador quando disponíveis.

### RF-081 — Mecanismos de verificação (`MUST`)
O processador deverá registrar quais mecanismos existem: assinatura, QR Code, código, conector ou nenhum.

### RF-082 — Domínios permitidos (`MUST`)
QR Codes e links somente poderão ser consultados após validação contra política de domínios, redirecionamentos e protocolos.

### RF-083 — Conectores (`SHOULD`)
Instituições prioritárias poderão ter conectores próprios.

### RF-084 — Ausência de verificação (`MUST`)
Certificados legíveis sem autenticação deverão ser marcados como não verificáveis.

## 10. Comprovante de residência

### RF-090 — Armazenamento (`MUST`)
O comprovante deverá passar por segurança de arquivo e ser armazenado sem OCR obrigatório.

### RF-091 — Resultado (`MUST`)
A resposta deverá indicar `STORED`, sem sugerir que o endereço foi validado.

### RF-092 — Visualização (`MUST`)
Arquivos deverão ser acessados por sessão ou URL temporária e auditada.

## 11. Revisão humana

### RF-100 — Fila de revisão (`MUST`)
Resultados inconclusivos deverão entrar em fila com motivo, prioridade e prazo.

### RF-101 — Evidências (`MUST`)
O revisor deverá visualizar apenas evidências necessárias, campos mascarados e arquivos autorizados.

### RF-102 — Decisão (`MUST`)
A decisão humana deverá registrar operador, data, justificativa e alterações.

### RF-103 — Dupla aprovação (`COULD`)
Organizações poderão exigir revisão por duas pessoas em casos de alto risco.

## 12. Resultado e integração

### RF-110 — Consulta (`MUST`)
O cliente poderá consultar sessão, progresso, resultado e motivos.

### RF-111 — Webhook (`SHOULD`)
A API deverá enviar webhook assinado ao concluir, com tentativas e mecanismo de replay seguro.

### RF-112 — Versionamento (`MUST`)
O resultado deverá informar versões do contrato, processador, regras e fornecedor.

### RF-113 — Evidências (`MUST`)
Cada verificação deverá registrar tipo, estado, fonte, horário e referência técnica.

### RF-114 — Retificação (`MUST`)
Correções não deverão sobrescrever silenciosamente resultados anteriores.

## 13. Retenção e exclusão

### RF-120 — Política (`MUST`)
Cada organização/finalidade deverá possuir retenção configurada.

### RF-121 — Exclusão automática (`MUST`)
Arquivos e dados expirados deverão ser excluídos por processo auditável.

### RF-122 — Bloqueio legal (`SHOULD`)
Quando aplicável, uma retenção legal autorizada poderá suspender exclusão automática.

### RF-123 — Exportação (`SHOULD`)
A API deverá permitir exportação autorizada de dados e histórico para atendimento a obrigações e direitos do titular.

## 14. Administração

### RF-130 — Configurações (`MUST`)
Administradores poderão configurar tipos permitidos, limites, retenção, regras e integrações.

### RF-131 — Credenciais externas (`MUST`)
Credenciais de fornecedores deverão ser referenciadas por secret manager, nunca armazenadas em texto no banco.

### RF-132 — Auditoria (`MUST`)
Criação, leitura, download, decisão, configuração e exclusão deverão gerar eventos de auditoria.