# DEPLOYMENT — Ajusta

> **Guia de Publicação, Empacotamento Release e Distribuição Comercial**  
> *Versão:* 1.0.0 | *Formatos Suportados:* Android App Bundle (AAB) & APK Universal

---

## 📦 1. Geração de Artefatos de Produção

### 1.1 Android App Bundle (.aab) — Recomendado para Google Play
O Google Play exige pacotes no formato App Bundle para otimizar o tamanho do download para cada dispositivo.

Para gerar via linha de comando:
```bash
gradle :app:bundleRelease
```
O arquivo gerado estará localizado em:
`app/build/outputs/bundle/release/app-release.aab`

### 1.2 APK Universal (.apk) — Para testes diretos ou lojas alternativas
```bash
gradle :app:assembleRelease
```
O arquivo gerado estará localizado em:
`app/build/outputs/apk/release/app-release.apk`

---

## 🔑 2. Configuração de Chave de Assinatura (Keystore)

O arquivo `app/build.gradle.kts` já possui um bloco de assinatura parametrizado via variáveis de ambiente do sistema operacional, evitando senhas expostas:

```kotlin
signingConfigs {
  create("release") {
    val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
    storeFile = file(keystorePath)
    storePassword = System.getenv("STORE_PASSWORD")
    keyAlias = "upload"
    keyPassword = System.getenv("KEY_PASSWORD")
  }
}
```

### Como gerar sua própria Keystore de produção:
```bash
keytool -genkey -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

---

## 🚀 3. Checklist de Lançamento na Google Play Store

1. **Conta de Desenvolvedor:** Registrada no [Google Play Console](https://play.google.com/console).
2. **Target SDK:** O projeto já está configurado para `targetSdk = 36` (Android 15/16), em total conformidade com as exigências anuais de compatibilidade da Google Play.
3. **Classificação de Conteúdo:** O app não contém conteúdo adulto ou violento (classificação indicativa Livre / All Ages).
4. **Política de Privacidade:** Obrigatória para aplicativos que utilizam permissão de câmera e IA. Prepare uma página simples declarando que as imagens são enviadas exclusivamente para o diagnóstico sob comando explícito do usuário.
5. **Permissões Declaradas:**
   * `CAMERA` (Runtime permission com tratamento gracioso de recusa).
   * `POST_NOTIFICATIONS` (Para lembretes agendados de reparos).
   * `INTERNET` (Para comunicação com a API do Gemini e Firestore).

---

## 🛒 4. Empacotamento para Marketplaces (Codester, Sell My Code, Flippa)

Ao vender o projeto em plataformas de código-fonte:

1. **Limpeza do Projeto (Clean Build):**
   Execute para remover caches locais temporários e reduzir o tamanho do ZIP:
   ```bash
   gradle clean
   ```
2. **Remoção de Arquivos Privados:**
   * Certifique-se de não incluir seu `.env` pessoal (o arquivo `.env.example` deve permanecer intacto).
   * Remova pastas `.gradle/` e `app/build/`.
3. **Estrutura Recomendada do Pacote ZIP de Venda:**
   ```
   Ajusta-Complete-Android-Project.zip
    ├── SourceCode/           # Código-fonte completo com Gradle e libs
    ├── Documentation/        # Todos os 18 arquivos .md gerados
    └── Screenshots/          # Capturas de tela promocionais do app
   ```
