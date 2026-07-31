# 03 — Requisitos não funcionais

## 1. Segurança

### RNF-001 — Transporte
Todo tráfego externo e interno deverá utilizar TLS. HTTP sem TLS deverá ser desabilitado em produção.

### RNF-002 — Criptografia em repouso
Arquivos, backups, volumes e bancos deverão usar criptografia em repouso. Chaves deverão ser administradas por KMS ou secret manager.

### RNF-003 — Segregação
Dados deverão ser segregados por organização em todas as consultas e chaves de armazenamento.

### RNF-004 — Menor privilégio
Usuários, serviços e processos deverão possuir somente permissões necessárias.

### RNF-005 — Logs
Logs não poderão conter imagens, PDF, CPF completo, número integral de documento, texto OCR completo, tokens ou segredos.

### RNF-006 — Arquivos hostis
Processamento deverá ocorrer em ambiente isolado, com limites de CPU, memória, tempo, páginas, resolução e tamanho descomprimido.

### RNF-007 — Dependências
Dependências deverão ser verificadas por SCA, atualização automática controlada e SBOM.

### RNF-008 — Auditoria imutável
Eventos de auditoria deverão ser protegidos contra alteração e possuir política de retenção própria.

## 2. Privacidade

### RNF-010 — Minimização
A API deverá coletar e retornar apenas dados necessários para a finalidade configurada.

### RNF-011 — Retenção
Arquivos temporários deverão expirar automaticamente. Prazos deverão ser configuráveis por organização e tipo.

### RNF-012 — Mascaramento
Interfaces e respostas administrativas deverão mascarar dados quando o acesso integral não for necessário.

### RNF-013 — Rastreabilidade
Toda visualização ou download de documento deverá ser registrada.

### RNF-014 — Ambientes
Documentos reais não poderão ser usados em desenvolvimento local ou testes automatizados comuns.

## 3. Disponibilidade e resiliência

### RNF-020 — Disponibilidade
Meta inicial da API em produção: 99,5% mensal, excluindo manutenções programadas. A meta deverá ser revisada após o piloto.

### RNF-021 — Degradação controlada
Falha de um fornecedor externo não deverá indisponibilizar upload, consulta de sessões ou documentos de estratégia `STORAGE_ONLY`.

### RNF-022 — Filas
Mensagens deverão ser persistentes, idempotentes e possuir dead-letter queue.

### RNF-023 — Retentativas
Retentativas deverão diferenciar erro transitório, limitação, indisponibilidade e erro permanente.

### RNF-024 — Recuperação
Metas iniciais: RPO de 24 horas e RTO de 8 horas. Valores deverão ser refinados com o negócio.

### RNF-025 — Backup
PostgreSQL e metadados críticos deverão possuir backups testados. A restauração deverá ser exercitada periodicamente.

## 4. Desempenho

### RNF-030 — Respostas síncronas
Operações de metadados deverão buscar p95 inferior a 500 ms, desconsiderando upload e dependências externas.

### RNF-031 — Upload
A API deverá suportar arquivos de até 20 MB inicialmente, com configuração por tipo.

### RNF-032 — Processamento
O processamento será assíncrono. O SLO inicial para documentos sem revisão será conclusão em até 2 minutos no p95, sujeito ao fornecedor.

### RNF-033 — Concorrência
O sistema deverá controlar concorrência por organização, fornecedor e processador para evitar esgotamento de recursos.

### RNF-034 — Escalabilidade
Workers de processamento deverão escalar horizontalmente sem duplicar decisões.

## 5. Manutenibilidade

### RNF-040 — Monólito modular
A primeira implementação deverá usar módulos com limites claros, sem comunicação por acesso indevido a tabelas internas.

### RNF-041 — Contratos
Endpoints públicos deverão ser descritos por OpenAPI e validados em CI.

### RNF-042 — Versionamento
Mudanças incompatíveis deverão criar nova versão de API. Campos opcionais poderão ser adicionados de forma compatível.

### RNF-043 — Fornecedores
Integrações deverão implementar portas internas, permitindo substituição por configuração.

### RNF-044 — Migrações
Banco deverá usar Flyway, com migrações revisáveis e nunca editadas após produção.

### RNF-045 — Código
Java deverá usar análise estática, formatação automatizada e regras de arquitetura verificadas por testes.

## 6. Observabilidade

### RNF-050 — Correlação
Toda requisição e processamento deverá possuir correlation ID e document session ID.

### RNF-051 — Métricas
Deverão existir métricas de volume, latência, erros, filas, decisões, revisão, fornecedor e retenção.

### RNF-052 — Tracing
Fluxos entre API, fila, storage e fornecedores deverão usar tracing distribuído quando possível.

### RNF-053 — Alertas
Alertas deverão cobrir indisponibilidade, crescimento de filas, falha de exclusão, malware, erros de assinatura e aumento de revisão.

### RNF-054 — Dados pessoais
Telemetria deverá usar identificadores técnicos e nunca atributos documentais brutos.

## 7. Compatibilidade

### RNF-060 — Site
O primeiro cliente deverá funcionar nas versões atuais dos principais navegadores Chromium, Firefox e Safari.

### RNF-061 — Mobile web
O fluxo de upload deverá ser utilizável em navegadores móveis Android e iOS, mesmo quando captura avançada não estiver disponível.

### RNF-062 — Formatos
JPEG, PNG e PDF deverão ser validados por conteúdo real, e não pela extensão.

## 8. Acessibilidade

### RNF-070 — Padrão
O site deverá buscar conformidade WCAG 2.2 nível AA.

### RNF-071 — Alternativas
Mensagens de captura e erro não poderão depender apenas de cor.

### RNF-072 — Teclado e leitor de tela
Upload, progresso, revisão e resultados deverão ser operáveis por teclado e possuir rótulos acessíveis.

## 9. Qualidade

### RNF-080 — Cobertura
Regras de negócio críticas deverão possuir cobertura unitária elevada e testes de mutação quando viável.

### RNF-081 — Contratos externos
Respostas de fornecedores deverão ser testadas por fixtures sanitizadas e contract tests.

### RNF-082 — Testes de segurança
Upload, autorização, SSRF, path traversal, arquivos maliciosos e isolamento entre organizações deverão ser testados.

### RNF-083 — Benchmark documental
Nenhum tipo deverá receber aprovação automática antes de atingir metas definidas em dataset de validação autorizado.

## 10. Governança

### RNF-090 — Decisões
Decisões arquiteturais relevantes deverão ser registradas em ADR.

### RNF-091 — Mudanças de regras
Toda alteração de decisão documental deverá ter versão, data, responsável e impacto esperado.

### RNF-092 — Fornecedores
Contratação deverá avaliar suporte ao Brasil, privacidade, localização de processamento, SLA, custos, retenção, subprocessadores e direito de auditoria.