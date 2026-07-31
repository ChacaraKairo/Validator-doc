# 01 — Visão, objetivos e escopo

## 1. Propósito

O Validator-doc será uma plataforma de validação documental acessível por API. Seu objetivo é receber documentos de pessoas, aplicar a estratégia adequada a cada tipo, produzir um resultado auditável e disponibilizar esse resultado para sistemas clientes.

O primeiro cliente será um site responsivo. A API, porém, deverá ser independente da interface e poderá ser integrada futuramente a sistemas de RH, ERPs, aplicativos móveis e parceiros.

## 2. Problema

Empresas que contratam ou cadastram profissionais precisam receber documentos em formatos variados, verificar qualidade, extrair informações, consultar fontes oficiais quando possível, preservar evidências e armazenar arquivos de forma segura. Fazer isso manualmente gera demora, inconsistência, risco de fraude e exposição indevida de dados pessoais.

## 3. Objetivos

- Padronizar o envio de documentos.
- Aceitar JPG, JPEG, PNG e PDF conforme o tipo documental.
- Separar extração, validação e armazenamento.
- Automatizar verificações possíveis sem esconder incertezas.
- Encaminhar casos inconclusivos para revisão humana.
- Manter evidências, histórico e auditoria.
- Fornecer contratos de API estáveis e versionados.
- Proteger dados pessoais e respeitar políticas de retenção.

## 4. Documentos no escopo

### 4.1 RG e CIN

- Entrada: frente e verso em JPG, JPEG ou PNG.
- Operações: controle de qualidade, identificação do modelo, extração de campos, validação sintática e comparação entre os lados.
- Tecnologia principal: SDK documental licenciado compatível com Web ou API self-hosted.

### 4.2 CNH

- Imagem: frente e verso em JPG, JPEG ou PNG.
- Digital: PDF original.
- Operações para imagem: captura, qualidade, extração e validação dos campos.
- Operações para PDF: preservação do original, assinatura digital, integridade, QR Code, texto e comparação de dados.

### 4.3 Carteira do Coren

- Entrada: JPG, JPEG ou PNG.
- Operações: qualidade, leitura de nome, número, UF e categoria, QR Code e consulta cadastral quando existir mecanismo autorizado.
- Observação: uma carteira legível não comprova que a inscrição permanece ativa.

### 4.4 Antecedentes criminais

- Entrada: PDF.
- Operações: identificação do emissor, extração de código, assinatura e QR Code, consulta à fonte oficial quando disponível e controle de validade.
- A primeira integração deverá priorizar um emissor definido pelo negócio, como a Polícia Federal.

### 4.5 Certificados de cursos

- Entrada: JPG, JPEG, PNG ou PDF.
- Operações: extração de nome, curso, instituição, carga horária e datas; verificação por assinatura, QR Code, código ou conector específico.
- Documentos sem mecanismo verificável deverão receber status `NOT_VERIFIABLE` ou `REVIEW_REQUIRED`, nunca `VALID` automaticamente.

### 4.6 Comprovante de residência

- Entrada: JPG, JPEG, PNG ou PDF.
- Operação: apenas recebimento, sanitização, antivírus, hash, armazenamento privado e auditoria.
- Fora de escopo: OCR, leitura do endereço e consulta ao emissor.

## 5. Fora do escopo inicial

- Reconhecimento facial e prova de vida.
- Comparação biométrica entre selfie e documento.
- Treinamento de modelo próprio de IA.
- Decisão jurídica sobre antecedentes.
- Validação universal de certificados de qualquer instituição.
- Contorno de CAPTCHA, autenticação ou proteção de portais externos.
- Aplicativo móvel nativo.
- Microsserviços completos no MVP.
- Blockchain como requisito de autenticidade.

## 6. Usuários e atores

- **Solicitante:** sistema ou usuário autorizado que cria uma sessão.
- **Titular:** pessoa a quem os documentos pertencem.
- **Operador:** profissional que acompanha e revisa documentos.
- **Administrador:** configura organizações, permissões, retenção e integrações.
- **Sistema cliente:** site, ERP ou aplicativo que consome a API.
- **Fornecedor documental:** SDK ou API licenciada.
- **Emissor:** órgão, conselho ou instituição que fornece mecanismo de verificação.

## 7. Fluxo de alto nível

1. O sistema cliente autentica-se.
2. Cria uma sessão com pessoa e tipo documental.
3. A API informa arquivos e formatos necessários.
4. O cliente envia os arquivos.
5. A API valida, calcula hash e armazena temporariamente.
6. O processamento é enfileirado.
7. O processador seleciona a estratégia do documento.
8. Campos e verificações são executados.
9. O motor de decisão produz resultado.
10. Casos incertos seguem para revisão humana.
11. A API disponibiliza resultado e evidências autorizadas.
12. A política de retenção determina arquivamento ou exclusão.

## 8. Estados de decisão

- `VALID`: evidências suficientes e verificações aprovadas.
- `INVALID`: evidência objetiva de falha, divergência ou adulteração.
- `PARTIALLY_VALIDATED`: parte relevante foi confirmada, mas nem todos os aspectos.
- `NOT_VERIFIABLE`: leitura realizada sem fonte ou mecanismo de autenticação.
- `REVIEW_REQUIRED`: decisão humana necessária.
- `UNSUPPORTED`: tipo, emissor, versão ou formato ainda não suportado.
- `FAILED`: falha técnica sem decisão documental.

## 9. Princípios de produto

- A API nunca deve transformar ausência de evidência em aprovação.
- Resultados devem explicar motivos e verificações executadas.
- O cliente não deve depender do objeto bruto de um fornecedor.
- Todo fornecedor deve ficar atrás de uma interface interna.
- Arquivos não devem ser públicos.
- A retenção deve ser configurável por organização e finalidade.
- O sistema deverá privilegiar reprocessamento idempotente e rastreável.

## 10. Critério de sucesso do MVP

O MVP estará apto para piloto quando conseguir:

- criar sessões documentais;
- receber os formatos definidos;
- processar RG/CIN/CNH por um fornecedor homologado;
- armazenar comprovante de residência sem leitura;
- validar estrutura, assinatura e QR Code de PDF;
- enviar casos inconclusivos para revisão;
- devolver resultado versionado;
- registrar auditoria sem expor dados em logs;
- executar testes automatizados e teste de carga básico;
- demonstrar exclusão e retenção de arquivos.