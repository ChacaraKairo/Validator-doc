# 11 — Operação e observabilidade

## 1. Objetivo

Definir como a plataforma será monitorada, suportada, recuperada e auditada em produção.

## 2. Ambientes

- `local`: dados sintéticos e Docker Compose.
- `test`: testes automatizados e integrações simuladas.
- `staging`: configuração próxima de produção e documentos autorizados controlados.
- `production`: dados reais, acesso restrito e controles completos.

Segredos, buckets, bancos e credenciais deverão ser separados por ambiente.

## 3. Health checks

- **liveness:** processo está vivo; não consulta dependências pesadas.
- **readiness:** API pode receber tráfego; verifica dependências essenciais com timeout.
- **startup:** permite inicialização mais longa sem reinício prematuro.

Endpoints não devem revelar versões sensíveis, hosts ou credenciais.

## 4. Métricas

### API

- requisições por endpoint/status;
- p50/p95/p99;
- autenticação e rate limit;
- upload por tipo/tamanho;
- erros por código.

### Processamento

- sessões por estado;
- duração por tipo/processador;
- tentativas e falhas;
- filas, idade da mensagem e DLQ;
- decisões por tipo;
- taxa de revisão e nova captura;
- indisponibilidade por fornecedor.

### Segurança e retenção

- malware detectado;
- arquivos em quarentena;
- falhas de isolamento/autorização;
- URLs temporárias emitidas;
- exclusões previstas, concluídas e falhas;
- objetos órfãos.

Métricas não devem usar nome, CPF ou número documental como labels.

## 5. Logs

Formato estruturado JSON:

- timestamp;
- level;
- service/version;
- environment;
- correlation ID;
- organization ID técnico;
- session/attempt ID;
- event name;
- error code;
- duração.

Aplicar redaction central e testes para impedir PII. Respostas brutas de fornecedores devem ficar fora dos logs.

## 6. Tracing

Propagar contexto entre site, API, banco, outbox, RabbitMQ, worker, storage e fornecedor. Sampling deve ser configurável e nunca incluir conteúdo documental.

## 7. Alertas iniciais

- API indisponível;
- erro 5xx acima do limite;
- fila acima da capacidade ou idade máxima;
- DLQ com mensagens;
- fornecedor externo degradado;
- storage/banco indisponível;
- ClamAV indisponível;
- job de retenção atrasado;
- falha de webhook persistente;
- aumento anormal de `INVALID` ou `REVIEW_REQUIRED`;
- tentativa de acesso entre organizações.

Cada alerta deverá possuir severidade, proprietário e runbook.

## 8. Runbooks mínimos

1. API indisponível.
2. Banco sem conexão.
3. RabbitMQ/DLQ.
4. Storage indisponível.
5. Fornecedor documental indisponível.
6. Emissor externo mudou o portal.
7. Malware detectado.
8. Vazamento ou acesso indevido suspeito.
9. Falha de exclusão/retensão.
10. Webhook acumulado.
11. Certificado/chave próximo do vencimento.
12. Restauração de backup.

## 9. Backup e recuperação

- backups automáticos do PostgreSQL;
- versionamento/backup de configurações e infraestrutura;
- política de objetos coerente com retenção;
- testes regulares de restauração;
- documentação de RPO/RTO;
- proibição de usar backup para contornar exclusão sem política definida.

## 10. Deploy

- imagens imutáveis e versionadas;
- SBOM;
- scan antes da publicação;
- migrações compatíveis com rollback operacional;
- implantação gradual;
- feature flags para novos processadores;
- rollback de aplicação sem apagar histórico;
- regras documentais versionadas separadamente.

## 11. Gestão de fornecedores

Monitorar latência, erros, quota, custo, alterações de contrato, versões de SDK/API e matriz de documentos suportados. Mudança de fornecedor deve passar por benchmark regressivo.

## 12. Webhooks

- filas separadas;
- assinatura HMAC;
- retentativa com backoff;
- limite de tentativas;
- DLQ;
- replay manual autorizado;
- registros de status sem armazenar resposta sensível;
- proteção contra destino privado/SSRF.

## 13. Retenção operacional

Job diário ou mais frequente deverá:

1. localizar objetos expirados;
2. respeitar legal hold;
3. marcar `DELETION_PENDING`;
4. remover objetos e artefatos;
5. confirmar inexistência;
6. atualizar `DELETED`;
7. gerar auditoria;
8. alertar falhas persistentes.

## 14. Suporte e incidentes

Chamados devem usar IDs técnicos. Documentos não devem ser anexados a ferramentas de suporte comuns. Acesso excepcional deve ser temporário, justificado e auditado.

## 15. SLOs iniciais

- disponibilidade mensal da API: 99,5%;
- p95 metadados: < 500 ms;
- p95 processamento sem revisão: < 2 minutos, sujeito a terceiros;
- início da análise de incidente crítico: conforme política empresarial;
- processamento de exclusão: dentro da janela definida pela retenção.

Os SLOs deverão ser revisados após medição do piloto.