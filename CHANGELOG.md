# CHANGELOG — Ajusta

> **Registro Histórico de Mudanças, Versões e Evolução do Produto**  
> *Padrão:* [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) & [Semantic Versioning](https://semver.org/lang/pt-BR/)

---

## [1.0.0] - 2026-10-07 — Lançamento Oficial "Ajusta Core & Atelier Suite"

### 🚀 Adicionado (Features Principais)
* **Diagnóstico Multimodal com Visão Computacional (Gemini Pro):**
  * Captura de múltiplas fotos via CameraX (frente, verso, detalhes de tecido).
  * Classificação automática de categoria, defeito e gravidade do problema.
  * Parser estruturado em JSON com tipagem forte e validação de segurança.
* **Motor Algorítmico de Precificação em Reais (R$):**
  * Tabela de mão de obra calibrada para o mercado brasileiro.
  * Fatores de multiplicação por tecido, acabamento especial e complexidade da peça.
* **Matriz de Decisão Econômica "Vale a Pena Reformar?":**
  * Comparativo automático entre o custo do reparo e o valor comercial da peça.
  * Cálculo de compensação para peças vintage e tecidos nobres.
* **Consultora Mestre em Alfaiataria (Chat AI):**
  * Ateliê conversacional com 3 modelos intercambiáveis: Flash Lite, Flash 3.5 e Pro 3.1.
  * Persona com profundo conhecimento técnico em tecidos, caimento e corte.
* **Ficha de Atendimento Digital & Exportação:**
  * Geração instantânea de ordem preliminar formatada para WhatsApp em 1 clique.
* **Módulo Ateliê Pro:**
  * Gestão de ordens de serviço (OS) com kanban de status (Pendente, Em Andamento, Pronto para Prova, Concluído, Entregue).
  * Registro de medidas e materiais necessários.
* **Diretório Especializado de Ateliês e Costureiras:**
  * Catálogo curado de profissionais por especialidade e localidade.
* **Central DIY de Tutoriais de Costura:**
  * Guias passo a passo ilustrados com checklist de materiais e níveis de dificuldade.
* **Sistema de Notificações e Lembretes Locais:**
  * Integração com `AlarmManager` e canais de notificação Android dedicados.
* **Dual Theme Dinâmico (Material Design 3):**
  * Paleta quente de ateliê (Warm Terracotta / Atelier Brass) com alternador rápido em todas as telas.
* **Persistência Híbrida Offline-First:**
  * Banco de dados local Room SQLite v2 com 3 entidades (`saved_analyses`, `diagnostic_results`, `professional_orders`).
  * Sincronização opcional com Google Cloud Firestore e login Google nativo via Credential Manager.

---

### 📚 Documentação (Protocolo EXIT SYSTEM)
* Criação dos 18 arquivos canônicos de transferência e especificação técnica para marketplaces (Build & Sell):
  * `README.md`, `PRODUCT_OVERVIEW.md`, `FEATURES.md`, `ARCHITECTURE.md`, `DATABASE.md`, `API.md`, `AUTHENTICATION.md`, `AI.md`, `BUSINESS_LOGIC.md`, `INSTALLATION.md`, `CONFIGURATION.md`, `DEPLOYMENT.md`, `TROUBLESHOOTING.md`, `DEPENDENCIES.md`, `KNOWN_LIMITATIONS.md`, `CHANGELOG.md`, `TRANSFER_GUIDE.md`, `LICENSE.md`.

---

### 🛡️ Transferibilidade & Segurança
* Isolamento total de chaves e variáveis via Secrets Gradle Plugin e `.env.example`.
* Remoção completa de referências a contas pessoais ou e-mails no código-fonte.
* Zero dependências de bibliotecas obsoletas ou licenças copyleft restritivas.
