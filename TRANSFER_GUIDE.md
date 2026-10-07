# TRANSFER GUIDE — Ajusta

> **Manual de Entrega ao Comprador e Guia de Transição de Titularidade**  
> *Versão:* 1.0.0 | *Objetivo:* Garantir 100% de Autonomia ao Novo Proprietário

---

## 🤝 1. O Que o Comprador Recebe

Ao adquirir o **Ajusta**, o comprador torna-se proprietário integral do ativo digital contendo:

1. **Código-Fonte Completo (100% Kotlin Nativo):**
   * Estrutura pronta para compilação em Android Studio.
   * Telas modernas desenvolvidas em Jetpack Compose e Material Design 3.
2. **Motores Algorítmicos Proprietários:**
   * Algoritmo de precificação têxtil em Reais (R$) com multiplicadores de tecido e acabamento.
   * Matriz de decisão financeira "Vale a Pena Reformar?".
3. **Integrações de Inteligência Artificial:**
   * Conectores prontos para Google Gemini (Vision e Chat) desacoplados via OkHttp.
4. **Bancos de Dados e Migrations:**
   * Esquema completo de banco local Room Database SQLite.
   * Conector de sincronização com Firebase Firestore.
5. **Assets e Identidade Visual:**
   * Ícones adaptativos configurados.
   * Sistema de cores com modo claro e escuro.
6. **Suíte Documental Completa:**
   * 18 documentos técnicos e comerciais padronizados para manutenção e evolução.

---

## 📋 2. Checklist de Transição Passo a Passo (Handover)

Siga os passos a seguir imediatamente após a aquisição:

### Passo 1: Configurar sua Chave Gemini Pessoal
1. Acesse o [Google AI Studio](https://aistudio.google.com) com a sua conta Google corporativa.
2. Crie uma nova API Key (o uso inicial é gratuito).
3. Crie o arquivo `.env` na raiz do projeto com sua nova chave:
   ```env
   GEMINI_API_KEY=sua_chave_aqui
   ```
4. A partir deste momento, todas as consultas do app estarão associadas ao seu próprio painel de uso.

### Passo 2: Alterar o Identificador do Aplicativo (`applicationId`)
Para publicar sob sua conta própria da Google Play Console:
1. Abra `app/build.gradle.kts`.
2. Localize `defaultConfig { applicationId = "com.aistudio.ajusta.crwpmt" }`.
3. Altere para o pacote de sua preferência, por exemplo:
   ```kotlin
   applicationId = "com.suaempresa.ajusta"
   ```
4. Sincronize o Gradle.

### Passo 3: Configurar sua Própria Assinatura de Release (Keystore)
1. Crie uma keystore de upload exclusiva para sua empresa (veja o comando em [DEPLOYMENT.md](./DEPLOYMENT.md)).
2. Configure as variáveis de ambiente `KEYSTORE_PATH`, `STORE_PASSWORD` e `KEY_PASSWORD` no seu servidor de CI/CD ou computador de build.

### Passo 4: Conectar ao seu Próprio Firebase (Opcional para Nuvem)
Se desejar habilitar login Google federado e backup em nuvem Firestore:
1. Crie um projeto próprio no [Firebase Console](https://console.firebase.google.com).
2. Registre o seu `applicationId` Android (`com.aistudio.ajusta.crwpmt` ou o seu novo ID).
3. Adicione o hash SHA-1 da sua máquina/keystore no console do Firebase.
4. Baixe o `google-services.json` oficial e copie para `app/google-services.json` (substituindo o modelo `app/google-services.json.template`).
5. Ative **Authentication > Google** e copie o **Web Client ID** para o arquivo `.env`:
   ```properties
   GOOGLE_WEB_CLIENT_ID=seu-id-web.apps.googleusercontent.com
   ```
6. Ative o **Firestore Database** e publique as regras prontas de `firestore.rules`.
7. O app compilará e sincronizará automaticamente com seu próprio projeto do Firebase. Zero dependência de contas ou acessos do desenvolvedor anterior.

---

## 🔒 3. Auditoria de Desacoplamento e Privacidade

* **Contas Pessoais da Criadora:** O projeto foi rigorosamente auditado para garantir que **não existe nenhuma amarração a e-mails pessoais, tokens secretos ou contas privadas** no repositório.
* **Templates Prontos:** Arquivos modelo (`.env.example`, `app/google-services.json.template`, `google-services.json.template`, `firestore.rules`) foram preparados especificamente para que o comprador tenha 100% de clareza e autonomia.
* **Custos Recorrentes Ocultos:** O app opera em modo local com zero custo de servidores para o proprietário. O único serviço externo é a API do Gemini, que possui cota gratuita para desenvolvimento e cobrança pay-as-you-go em produção.

---

## 🛠️ 4. Suporte e Continuidade Técnica

Como o projeto segue rigorosamente os padrões oficiais recomendados pelo Google Android (MVVM, Kotlin Coroutines, Jetpack Compose, Room), qualquer desenvolvedor Android pleno ou sênior no mercado terá facilidade imediata para:
* Adicionar novas telas ou fluxos.
* Integrar sistemas de cobrança (Google Play Billing ou Mercado Pago).
* Conectar o app a um sistema ERP de ateliê já existente.
