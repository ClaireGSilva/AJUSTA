# TROUBLESHOOTING — Ajusta

> **Guia de Diagnóstico e Resolução de Problemas Frequentes**  
> *Versão:* 1.0.0 | *Público:* Desenvolvedores e Novos Proprietários

---

## 🔍 1. Problemas de Inteligência Artificial e Rede

### ❌ Erro: `java.lang.IllegalStateException: API_KEY_NOT_CONFIGURED`
* **Causa:** O aplicativo foi executado sem o preenchimento da chave `GEMINI_API_KEY` ou o valor configurado ainda é o placeholder de exemplo.
* **Solução:**
  1. Verifique se o arquivo `.env` existe na pasta raiz do projeto.
  2. Garanta que a linha `GEMINI_API_KEY=AIzaSy...` contenha uma chave válida gerada no Google AI Studio.
  3. No Android Studio, clique em **File > Sync Project with Gradle Files** e recompile o app.

---

### ❌ Erro: `Gemini API call failed: code=429` (Quota Exceeded)
* **Causa:** Limite de requisições por minuto do tier gratuito do Google AI Studio atingido.
* **Solução:**
  * Aguarde 60 segundos antes de tentar novamente.
  * Para ambientes de produção com alto volume de usuários, vincule uma conta de faturamento (Billing) no Google Cloud Console para habilitar o plano Pay-as-you-go sem limites restritivos.

---

## 📸 2. Câmera e Permissões

### ❌ A tela de câmera fica preta ou trava
* **Causa:** O emulador Android utilizado não possui uma webcam emulada configurada, ou a permissão de câmera foi negada.
* **Solução:**
  * No emulador do Android Studio, vá em **Virtual Device Manager > Edit (Lápis) > Show Advanced Settings > Camera** e certifique-se de que a câmera frontal e traseira estão configuradas para **Webcam0** ou **VirtualScene**.
  * Em aparelhos físicos, acesse as Configurações do Android > Aplicativos > Ajusta > Permissões e autorize o acesso à Câmera.

---

## 🗄️ 3. Banco de Dados Local (Room Database)

### ❌ Erro: `IllegalStateException: Room cannot verify the data integrity / Migration failed`
* **Causa:** Alteração na estrutura de classes das entidades sem incremento do número da versão do banco ou sem criação de migration.
* **Solução:**
  * O `AppDatabase.kt` já vem configurado com `.fallbackToDestructiveMigration(dropAllTables = true)`. Em ambiente de desenvolvimento, desinstale o app do emulador/aparelho para recriar as tabelas com o novo schema limpo.
  * Para produção com dados reais de usuários, implemente a classe de migração explícita `Migration(versionAtual, novaVersao)`.

---

## 🔔 4. Notificações no Android 13+ (API 33+)

### ❌ Os lembretes de reparo e dicas não aparecem na barra de notificações
* **Causa:** A partir do Android 13, a permissão `POST_NOTIFICATIONS` é uma permissão de tempo de execução (Runtime Permission).
* **Solução:**
  * Abra a tela de **Configurações de Notificações** no app e clique no banner de ativação para conceder a permissão.
  * Verifique se o aparelho não está no modo "Não Perturbe" ou com otimização agressiva de bateria desativando alarmes em segundo plano.

---

## ☕ 5. Erros de Compilação do Gradle

### ❌ Erro: `Unsupported class file major version` ou incompatibilidade de Java
* **Causa:** O Android Studio está configurado para compilar com uma versão antiga do JDK (ex: Java 8 ou Java 11).
* **Solução:**
  * Acesse **Settings (Preferences) > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.
  * Selecione **Embedded JDK (JDK 17 ou JDK 21)**.

---

## 🔐 6. Firebase, Autenticação e Sincronização em Nuvem

### ❌ Erro: `GetCredentialException: Developer Error (10)` ou `12500` no Google Sign-In
* **Causa:** O certificado digital (SHA-1) da sua chave de compilação não está cadastrado no Firebase Console, ou há divergência no `applicationId`.
* **Solução:**
  1. Obtenha o SHA-1 da sua máquina executando no terminal:
     ```bash
     keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
  2. Acesse o [Firebase Console](https://console.firebase.google.com) > **Configurações do Projeto** > seu app Android.
  3. Clique em **Adicionar impressão digital** e cole o valor SHA-1.
  4. Baixe novamente o `google-services.json` atualizado e substitua em `app/google-services.json`.
  5. Certifique-se de que o `GOOGLE_WEB_CLIENT_ID` no `.env` foi copiado de **Authentication > Sign-in method > Google > Configuração do SDK da Web**.

---

### ⚠️ Aviso no Console do Gradle: `File google-services.json is missing`
* **Causa:** O projeto utiliza `MissingGoogleServicesStrategy.WARN` para permitir desenvolvimento 100% offline e independente sem exigir conta de nuvem imediata.
* **Solução:**
  * Este aviso é normal e intencional. O aplicativo executa perfeitamente em modo local-first utilizando o banco de dados Room SQLite.
  * Para silenciar o aviso e ativar os recursos em nuvem, siga o passo a passo em [CONFIGURATION.md](./CONFIGURATION.md) copiando o seu `google-services.json` oficial para a pasta `/app`.

---

### ❌ Erro no Firestore: `PERMISSION_DENIED: Missing or insufficient permissions`
* **Causa:** As regras de segurança do Firestore no console não foram publicadas ou não autorizam a operação.
* **Solução:**
  1. No Firebase Console, acesse **Build > Firestore Database > Regras (Rules)**.
  2. Cole o conteúdo de `firestore.rules` da raiz do projeto:
     ```javascript
     rules_version = '2';
     service cloud.firestore {
       match /databases/{database}/documents {
         match /users/{userId}/{document=**} {
           allow read, write: if request.auth != null && request.auth.uid == userId;
         }
       }
     }
     ```
  3. Clique em **Publicar**. Certifique-se de que o usuário está autenticado antes de invocar `FirestoreSyncService`.
