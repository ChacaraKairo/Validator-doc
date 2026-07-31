# 08 — Segurança, privacidade e LGPD

## 1. Classificação dos dados

O projeto manipulará documentos de identidade, registros profissionais, antecedentes, certificados e comprovantes. Esses arquivos contêm dados pessoais e podem revelar informações de alto impacto para o titular. O tratamento deverá ser definido com apoio jurídico e governança interna.

## 2. Papéis e finalidade

Antes da produção, registrar:

- controlador e operadores;
- finalidade de cada tipo documental;
- hipótese legal aplicável;
- dados estritamente necessários;
- prazo de retenção;
- destinatários e compartilhamentos;
- fornecedores e subprocessadores;
- canal para direitos do titular;
- responsável por incidentes e privacidade.

O software não determina sozinho a base legal nem a adequação do uso de antecedentes em contratação.

## 3. Princípios

- finalidade e adequação;
- necessidade/minimização;
- transparência;
- segurança e prevenção;
- não discriminação;
- responsabilização;
- rastreabilidade das decisões.

## 4. Controles de acesso

- OpenID Connect para usuários.
- OAuth 2.0 client credentials para sistemas.
- MFA para administradores e revisores.
- RBAC com escopos por organização.
- autorização verificada em todo recurso, inclusive storage.
- sessões curtas e revogáveis.
- revisão periódica de permissões.
- proibição de contas compartilhadas.

## 5. Proteção de dados

### Em trânsito

- TLS moderno.
- HSTS no site.
- mTLS em integrações internas de alto risco, quando justificável.

### Em repouso

- criptografia de banco, volumes, backups e objetos;
- KMS/secret manager;
- rotação de chaves;
- campos de alta sensibilidade criptografados na aplicação;
- URLs assinadas curtas e sem acesso público.

### Em logs

Permitido: IDs técnicos, tipo documental, estado, duração, códigos de erro.

Proibido: arquivo, texto integral, CPF completo, número completo, endereço, nome integral quando desnecessário, QR bruto, token, senha, chave e resposta externa completa.

## 6. Segurança de upload

- allowlist de formatos por tipo;
- detecção MIME por conteúdo;
- limites de tamanho, páginas, dimensões e descompressão;
- antivírus;
- decodificação real de imagens;
- remoção de EXIF;
- PDF em sandbox;
- bloqueio de scripts, anexos e conteúdo ativo para visualização;
- nomes de armazenamento gerados internamente;
- quarentena para arquivos suspeitos.

## 7. PDFs e assinaturas

O original deverá ser preservado quando a assinatura for validada. A visualização deve utilizar cópia segura. Validadores precisam considerar cadeia, revogação, carimbo de tempo, política de confiança e alterações incrementais. “Assinatura presente” não equivale a “assinatura confiável”.

## 8. QR Codes e URLs

QR Code é entrada não confiável. Antes de qualquer consulta:

- permitir apenas HTTPS, salvo exceção formal;
- normalizar URL;
- bloquear IP privado, loopback, link-local e metadata endpoints;
- resolver DNS de forma segura;
- limitar redirecionamentos;
- validar domínio final contra allowlist;
- limitar resposta e tempo;
- não executar JavaScript remoto;
- não enviar dados adicionais sem necessidade.

## 9. Retenção e descarte

Definir prazos por tipo e finalidade. O processo automático deverá excluir:

- originais;
- sanitizados;
- miniaturas;
- artefatos OCR;
- respostas brutas de fornecedor;
- caches;
- backups após sua janela própria.

A exclusão deve gerar evidência técnica sem reter o conteúdo excluído.

## 10. Direitos do titular

A arquitetura deverá permitir localizar dados por referência interna autorizada, exportar registros aplicáveis, corrigir metadados sem apagar histórico e executar exclusão quando juridicamente permitida. Solicitações deverão ser autenticadas e auditadas.

## 11. Fornecedores

Avaliar:

- local de processamento e armazenamento;
- retenção de imagens;
- uso para treinamento;
- subprocessadores;
- criptografia;
- SLA e incidentes;
- exclusão;
- suporte a documentos brasileiros;
- contrato de operador;
- portabilidade e encerramento.

Nenhum documento real deverá ser enviado durante prova de conceito sem autorização, contrato e ambiente apropriado.

## 12. Ameaças principais

- acesso entre organizações;
- IDOR em documentos e sessões;
- upload malicioso;
- PDF bomb;
- SSRF por QR/link;
- vazamento em logs;
- URL assinada longa;
- credencial de fornecedor exposta;
- resposta adulterada de webhook;
- reprocessamento duplicado;
- operador interno acessando documento sem necessidade;
- retenção indefinida;
- aprovação indevida por confiança excessiva no OCR.

## 13. Auditoria

Eventos mínimos:

- autenticação e falhas;
- criação e alteração de configuração;
- criação de sessão;
- upload, quarentena e exclusão;
- início/fim de processamento;
- consulta e visualização;
- decisão automática e humana;
- emissão de URL temporária;
- entrega de webhook;
- alterações de permissão e retenção.

## 14. Incidentes

Manter runbook para detecção, contenção, preservação de evidências, avaliação de impacto, comunicação interna, obrigações regulatórias, comunicação a titulares quando aplicável, correção e retrospectiva.

## 15. Checklist antes da produção

- análise jurídica e de privacidade concluída;
- política de retenção aprovada;
- fornecedores contratados e avaliados;
- threat model revisado;
- teste de invasão realizado;
- restauração de backup testada;
- exclusão automática testada;
- segregação multi-tenant testada;
- runbook de incidente exercitado;
- equipe de revisão treinada;
- termos e avisos do site publicados.