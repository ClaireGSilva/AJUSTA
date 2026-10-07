# CONFIGURATION — Ajusta

> **Guia de Configuração, Customização, Branding e Variáveis de Ambiente**  
> *Versão:* 1.0.0 | *Padrão:* Separação Estrita de Código e Configuração

---

## 🔒 1. Gestão de Segredos e Chaves de API

O **Ajusta** utiliza o **Secrets Gradle Plugin** (`com.google.android.libraries.mapsplatform.secrets-gradle-plugin`), garantindo que nenhuma chave de API fique exposta em repositórios Git públicos.

### 1.1 Arquivo `.env` (Raiz do Projeto)
O arquivo `.env` é ignorado pelo Git (definido no `.gitignore`) e serve para injetar as credenciais no momento do build. Use o arquivo modelo `.env.example` como base:

```env
# Chave da API Google Gemini (Obrigatória para IA e Visão Computacional)
GEMINI_API_KEY=AIzaSy...sua_chave...

# ID do Cliente Web para Login Google / Jetpack Credential Manager (Opcional para Nuvem)
GOOGLE_WEB_CLIENT_ID=000000000000-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com
```

### 1.2 Acesso em Código Kotlin
No código, os segredos são acessados de forma fortemente tipada:
```kotlin
val apiKey = BuildConfig.GEMINI_API_KEY
val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
```

Se a chave do Gemini estiver vazia ou for igual ao placeholder padrão (`MY_GEMINI_API_KEY`), o serviço `GeminiService` lança uma exceção informativa alertando o desenvolvedor para configurar o `.env`. Se `GOOGLE_WEB_CLIENT_ID` não for informado, o login com Google adota o fallback graceful para manter a aplicação 100% responsiva em modo local.

---

## 🎨 2. Customização de Marca (Rebranding & White-Label)

Para compradores que desejam revender ou lançar o aplicativo sob uma nova marca própria, siga os passos abaixo:

### 2.1 Nome do Aplicativo
1. **Nome de Exibição no Android Launcher:**
   * Arquivo: `app/src/main/res/values/strings.xml`
   * Altere a tag: `<string name="app_name">Ajusta</string>`
2. **Nome do Projeto no Gradle:**
   * Arquivo: `settings.gradle.kts`
   * Altere: `rootProject.name = "Ajusta"`
3. **Metadados da Plataforma:**
   * Arquivo: `metadata.json`
   * Altere a propriedade `"name"` para coincidir com o `strings.xml`.

### 2.2 Identificador Único do Pacote (`applicationId`)
Para publicar sua própria versão exclusiva na Google Play Store sem conflitos com outros apps:
* Arquivo: `app/build.gradle.kts`
* Modifique `defaultConfig { applicationId = "com.suaempresa.seuapp" }`
* *(Atenção: Mantenha `namespace = "com.example"` inalterado para preservar os imports de classes internas e o R gerado).*

### 2.3 Cores e Identidade Visual (Tema)
* Arquivo: `app/src/main/java/com/example/ui/theme/Color.kt`
* Altere os tokens de cores primárias:
  * `Warm Terracotta` (Cor primária dos botões de ação e cabeçalhos).
  * `Atelier Brass` (Dourado de acento para selos e bordas de ateliê).
  * `Surface` e `Background` (Fundos claro e escuro).

---

## 🔥 3. Configuração do Firebase & Arquivo `google-services.json` (Transferibilidade)

O aplicativo foi projetado com arquitetura **Offline-First / Zero-Lock-In**. Ele roda de imediato sem o Firebase. Quando você desejar ativar o ecossistema de nuvem no seu próprio projeto:

### 3.1 Modelo Fornecido (`google-services.json.template`)
O repositório inclui os arquivos:
* `/app/google-services.json.template`
* `/google-services.json.template`

Esse modelo documenta exatamente a estrutura esperada pelo plugin Gradle `com.google.gms.google-services`:
```json
{
  "project_info": {
    "project_number": "000000000000",
    "project_id": "your-firebase-project-id",
    "storage_bucket": "your-firebase-project-id.firebasestorage.app"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:000000000000:android:0000000000000000000000",
        "android_client_info": {
          "package_name": "com.aistudio.ajusta.crwpmt"
        }
      },
      ...
    }
  ]
}
```

### 3.2 Passo a Passo de Integração Própria:
1. Acesse o [Firebase Console](https://console.firebase.google.com) com sua conta Google.
2. Crie um projeto Firebase (ex: `meu-app-ajustes`).
3. Adicione um app Android informando o seu `applicationId` (`com.aistudio.ajusta.crwpmt` ou o novo configurado por você).
4. Obtenha a assinatura SHA-1 da sua chave (debug ou release):
   ```bash
   keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   Adicione o hash SHA-1 nas configurações do app no console do Firebase.
5. Baixe o arquivo `google-services.json` gerado pelo Firebase e salve em:
   ```
   /app/google-services.json
   ```
6. Ative os serviços desejados no console:
   * **Authentication:** Ative o provedor **Google**. Copie o **Web Client ID** e coloque em `.env` (`GOOGLE_WEB_CLIENT_ID=...`).
   * **Firestore Database:** Crie a base e publique as regras presentes em `firestore.rules`.
7. Execute o build (`gradle assembleDebug`). O plugin do Google Services detectará o novo arquivo e configurará os serviços automaticamente.
