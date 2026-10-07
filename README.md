# Ajusta - Assistente Inteligente de Ajustes e Costura

> **EXIT SYSTEM: BUILD & SELL EDITION**  
> *Produto digital completo, modular e transferível, desenvolvido em Kotlin e Jetpack Compose para ecossistema Android.*

[![Android](https://img.shields.io/badge/Platform-Android_24%2B-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-BOM_2024.09-blue.svg)](https://developer.android.com/jetpack/compose)
[![AI](https://img.shields.io/badge/Google_Gemini-Multimodal_Vision_%2B_Chat-orange.svg)](https://ai.google.dev)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM_%2B_Clean-brightgreen.svg)]()
[![Transferability](https://img.shields.io/badge/Transferability-100%25_Ready-gold.svg)]()

---

## 📌 Visão Geral do Produto

**Ajusta** é uma solução móvel completa desenvolvida para transformar a relação das pessoas com as suas roupas e conectar clientes ao universo da costura, alfaiataria e reparo sustentável.

O aplicativo une **Visão Computacional Multimodal (Google Gemini Pro/Flash)** com um **Motor Algorítmico de Precificação Calibrado**, permitindo que o usuário fotografe uma peça com defeito (bainha desfeita, zíper travado, ajuste de cintura, rasgos, forro danificado), receba um diagnóstico técnico instantâneo, descubra uma estimativa justa de preço em Reais (R$) e avalie a viabilidade econômica através do índice **"Vale a Pena Reformar?"**.

Além disso, o **Ajusta** conta com uma suíte dedicada para profissionais de costura (gestão de pedidos, ficha de atendimento/Ordem de Serviço digital com compartilhamento via WhatsApp) e tutoriais DIY passo a passo.

---

## 💎 Destaques Comerciais (Build & Sell)

* **Pronto para Comercialização / Transferência:** Estruturado sem acoplamento a contas pessoais ou segredos embutidos em código-fonte.
* **100% Offline-First com Suporte Opcional a Nuvem:** Funciona perfeitamente sem login (armazenamento local via Room Database SQLite) e oferece sincronização em nuvem e login Google via Firebase Firestore e Credential Manager.
* **Design System Material 3 Refinado:** Estética de ateliê elegante (Warm Terracotta / Atelier Brass) com alternância fluida entre modo claro e escuro.
* **Monetização Flexível:** Modelo ideal para venda em marketplaces (Codester, Sell My Code, Flippa, Acquire) ou conversão em aplicativo SaaS/Freemium.

---

## 🚀 Quick Start (Início Rápido)

1. **Pré-requisitos:** Android Studio (Ladybug / Iguana ou mais recente), JDK 17/21, Android SDK API 36 (minSdk 24).
2. **Configuração de Ambiente:**
   ```bash
   cp .env.example .env
   # Adicione sua chave da API Gemini no arquivo .env:
   # GEMINI_API_KEY=sua_chave_aqui
   ```
3. **Build e Execução:**
   ```bash
   gradle assembleDebug
   ```

Para instruções detalhadas de configuração e execução, consulte [INSTALLATION.md](./INSTALLATION.md) e [CONFIGURATION.md](./CONFIGURATION.md).

---

## 📚 Índice da Documentação Oficial

Toda a documentação técnica, operacional e de transferência foi estruturada segundo o protocolo **EXIT SYSTEM**:

| Documento | Descrição |
|---|---|
| 📄 [PRODUCT_OVERVIEW.md](./PRODUCT_OVERVIEW.md) | Visão executiva, público-alvo, persona, proposta de valor e mercado. |
| ⚡ [FEATURES.md](./FEATURES.md) | Inventário completo de todas as telas, funcionalidades e fluxos do app. |
| 🏛️ [ARCHITECTURE.md](./ARCHITECTURE.md) | Padrões de arquitetura (MVVM, UDF, Camadas), fluxo de dados e pacotes. |
| 🗄️ [DATABASE.md](./DATABASE.md) | Modelagem relacional local (Room DB), entidades, DAOs e Firestore. |
| 🌐 [API.md](./API.md) | Contratos de API REST (Gemini v1beta), serviços externos e endpoints. |
| 🔐 [AUTHENTICATION.md](./AUTHENTICATION.md) | Modelo híbrido (Off-line sem login + Google Sign-In via Credential Manager). |
| 🤖 [AI.md](./AI.md) | Documentação aprofundada de IA: modelos Gemini, prompts, custos e substituição. |
| 💼 [BUSINESS_LOGIC.md](./BUSINESS_LOGIC.md) | Motores de cálculo de preços (R$), matriz "Vale a Pena" e regras de negócio. |
| 🛠️ [INSTALLATION.md](./INSTALLATION.md) | Guia passo a passo de setup, compilação e execução no Android Studio. |
| ⚙️ [CONFIGURATION.md](./CONFIGURATION.md) | Gestão de variáveis (.env), Secrets Plugin e parametrização. |
| 📦 [DEPLOYMENT.md](./DEPLOYMENT.md) | Geração de APK/AAB release, assinatura de keystore e publicação na Google Play. |
| 🔍 [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) | Resolução de problemas comuns, erros de compilação e runtime. |
| 📦 [DEPENDENCIES.md](./DEPENDENCIES.md) | Inventário de bibliotecas, plugins, licenças e justificativas técnicas. |
| ⚠️ [KNOWN_LIMITATIONS.md](./KNOWN_LIMITATIONS.md) | Limitações conhecidas e escopo futuro registrado como TODO. |
| 📝 [CHANGELOG.md](./CHANGELOG.md) | Histórico de versões, lançamentos e notas de desenvolvimento. |
| 🤝 [TRANSFER_GUIDE.md](./TRANSFER_GUIDE.md) | Manual de entrega ao comprador: checklist de transição e troca de titularidade. |
| ⚖️ [LICENSE.md](./LICENSE.md) | Termos de licença comercial e direitos de uso do software. |

---

## 🛡️ Princípio: Build for Transferability

Este projeto segue estritamente a diretriz de **Transferibilidade Imediata**:
* Nenhuma credencial pessoal ou chave fixa no repositório.
* Arquitetura desacoplada e modular.
* Componentes tipados e testáveis.
* Documentação viva que acompanha o código.
