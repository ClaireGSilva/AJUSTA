# AUTHENTICATION — Ajusta

> **Documentação de Identidade, Autenticação e Segurança**  
> *Versão:* 1.0.0 | *Padrão:* Modelo Híbrido (Local First + Google Identity Services)

---

## 🔐 Filosofia de Autenticação

Para garantir a melhor taxa de conversão e retenção de usuários, o **Ajusta** adota uma estratégia **Zero-Barrier to Entry** (sem barreiras de entrada):
1. **Modo Visitante / Offline (Padrão):** O usuário baixa o aplicativo e pode fotografar peças, receber diagnósticos, consultar preços, conversar com o Ateliê AI e salvar ordens de serviço imediatamente, sem tela de bloqueio ou cadastro obrigatório.
2. **Modo Conectado (Opcional):** O usuário pode conectar sua conta Google para sincronizar seu histórico e ordens de serviço entre dispositivos via Firebase.

---

## 🛠️ Stack Tecnológica de Autenticação

* **SDK:** `androidx.credentials:credentials:1.5.0` (Jetpack Credential Manager)
* **Provedor de Identidade:** `com.google.android.libraries.identity.googleid:googleid:1.1.1` (`GetGoogleIdOption`)
* **Backend de Autenticação:** `com.google.firebase:firebase-auth`
* **Implementação:** `com.example.auth.FirebaseAuthService`

---

## 👤 Modelo de Dados do Usuário (`UserProfile`)

```kotlin
data class UserProfile(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean
)
```

---

## 🔄 Fluxos de Autenticação

### 1. Inicialização do App
Ao iniciar a aplicação, o `FirebaseAuthService` verifica o estado atual do `FirebaseAuth.getInstance().currentUser`. 
* Se houver um usuário logado (Google ou Anônimo), emite o `UserProfile`.
* Se for nulo, a aplicação permanece no estado local com persistência 100% ativa via Room Database.

### 2. Login com Google (Fluxo Moderno Android 14+)
1. A UI invoca `FirebaseAuthService.signInWithGoogle(activity, webClientId)`.
2. O `CredentialManager` exibe o diálogo nativo do sistema operacional com as contas Google registradas no aparelho.
3. Ao selecionar a conta, a aplicação recebe o ID Token criptografado do Google Identity.
4. O token é trocado no Firebase Auth por uma credencial segura (`GoogleAuthProvider.getCredential(idToken, null)`).
5. O estado do usuário é propagado automaticamente via `StateFlow<UserProfile?>` para toda a UI em Jetpack Compose.

### 3. Logout e Troca de Conta
* O método `FirebaseAuthService.signOut()` invalida a sessão local do Firebase.
* Os dados locais do Room Database permanecem no dispositivo do usuário, garantindo que ele não perca o histórico previamente salvo.

---

## 🚀 Guia de Integração do Firebase para o Novo Proprietário (Transferibilidade)

Para vincular o aplicativo ao seu próprio projeto Firebase sem depender de nenhuma conta anterior:

### Passo 1: Criar ou Selecionar Projeto no Console do Firebase
1. Acesse o [Firebase Console](https://console.firebase.google.com/).
2. Clique em **Adicionar projeto** e dê um nome ao seu projeto (ex: `ajusta-production`).
3. Desative ou ative o Google Analytics conforme sua preferência.

### Passo 2: Registrar o Aplicativo Android
1. Na visão geral do projeto, clique no ícone do **Android** para adicionar um app.
2. No campo **Nome do pacote Android**, insira o `applicationId` configurado em `app/build.gradle.kts`:
   ```
   com.aistudio.ajusta.crwpmt
   ```
   *(Caso altere o `applicationId` para sua própria empresa, use o novo ID).*
3. No campo **Certificado de autenticação SHA-1** (essencial para Google Sign-In):
   * Para ambiente de desenvolvimento (debug):
     ```bash
     keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
   * Copie o valor do hash **SHA-1** exibido e cole no Firebase Console.
   * Adicione também a chave **SHA-256** se desejar proteção avançada e App Check.

### Passo 3: Baixar e Instalar o `google-services.json`
1. No passo 2 do assistente do Firebase, baixe o arquivo oficial **`google-services.json`**.
2. Copie o arquivo baixado para a pasta `/app` do projeto:
   ```bash
   cp ~/Downloads/google-services.json ./app/google-services.json
   ```
   *(Existe um modelo de referência em `app/google-services.json.template` e `google-services.json.template` detalhando a estrutura esperada).*

### Passo 4: Ativar o Google Sign-In no Firebase Auth
1. No menu lateral do Firebase Console, vá em **Build > Authentication > Sign-in method**.
2. Clique em **Adicionar novo provedor** e selecione **Google**.
3. Ative o botão **Ativar**.
4. Defina o e-mail de suporte ao projeto.
5. Em **Configuração do SDK da Web**, copie o **ID do cliente Web** (*Web Client ID* gerado automaticamente pelo Firebase).

### Passo 5: Configurar o Web Client ID no `.env`
1. Abra o arquivo `.env` na raiz do projeto (ou configure no painel de segredos do AI Studio):
   ```properties
   GOOGLE_WEB_CLIENT_ID=seu-id-de-cliente-web.apps.googleusercontent.com
   ```
2. O aplicativo injeta esse valor dinamicamente via `BuildConfig.GOOGLE_WEB_CLIENT_ID`, garantindo total desacoplamento e portabilidade.

### Passo 6: Compilar e Testar
1. Execute a compilação:
   ```bash
   gradle assembleDebug
   ```
2. Ao abrir o aplicativo e tocar no botão de login com Google no perfil ou no cabeçalho, o fluxo nativo do Google Identity abrirá imediatamente conectado ao seu novo backend.

---

## 🛡️ Diretrizes de Transferibilidade e Segurança

* **Nenhuma Credencial Pessoal:** O código não possui client IDs, senhas ou tokens hardcoded.
* **Isolamento via Variáveis:** Todas as credenciais de autenticação são lidas de `google-services.json` e `.env` (`BuildConfig`).
* **Resiliência e Fallback:** Se `google-services.json` não estiver presente durante desenvolvimento, o Gradle compila normalmente através de `MissingGoogleServicesStrategy.WARN`, operando em modo offline/local com Room Database.
* **Tokens em Memória:** Todos os tokens de sessão são mantidos em memória volátil gerenciados pelo SDK do Google Identity / Firebase.
* **Zero Permissões Invasivas:** O login não solicita permissões adicionais além da autenticação federada.
