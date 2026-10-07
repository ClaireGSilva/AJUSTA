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

## 🛡️ Diretrizes de Transferibilidade e Segurança

* **Nenhuma Credencial Pessoal:** O código não possui client IDs ou senhas hardcoded.
* **Tokens em Memória:** Todos os tokens de sessão são mantidos em memória volátil e pelo SDK oficial do Google Play Services.
* **Sem Permissões Invasivas:** O login não solicita permissões adicionais de contatos ou agenda.
