# INSTALLATION — Ajusta

> **Guia Passo a Passo de Instalação, Compilação e Execução**  
> *Versão:* 1.0.0 | *Plataforma Alvo:* Android 7.0+ (API 24 até API 36)

---

## 💻 1. Requisitos de Sistema

Para compilar e executar o projeto com excelência, certifique-se de possuir:

* **Sistema Operacional:** Windows 10/11 (64-bit), macOS (Intel ou Apple Silicon) ou Linux (Ubuntu 20.04+).
* **IDE Recomendada:** [Android Studio Ladybug (2024.2+)](https://developer.android.com/studio) ou mais recente.
* **Java Development Kit (JDK):** OpenJDK 17 ou OpenJDK 21 (o Android Studio já inclui o JetBrains Runtime integrado).
* **Android SDK:**
  * Android SDK Platform 36 (com minor API level 1).
  * Android SDK Build-Tools 36.x.
  * Android NDK (não obrigatório; o projeto é 100% Kotlin nativo).
* **Hardware Mínimo Recomendado:** 8 GB de memória RAM (16 GB recomendado) e 10 GB de espaço livre em disco.

---

## 📥 2. Clonagem e Abertura do Projeto

1. Baixe os arquivos do projeto ou clone o repositório em uma pasta local sem caracteres especiais ou espaços no caminho:
   ```bash
   cd /seu/diretorio/de/projetos
   git clone <URL_DO_REPOSITORIO> ajusta-android
   cd ajusta-android
   ```

2. Abra o **Android Studio**.
3. Na tela de boas-vindas, selecione **Open** e navegue até a pasta raiz do projeto (onde está o arquivo `settings.gradle.kts`).
4. Aguarde a indexação inicial e o download automático dos plugins do Gradle.

---

## ⚙️ 3. Configuração de Variáveis de Ambiente

Antes da primeira compilação, você deve criar o arquivo `.env`:

1. Duplique o modelo `.env.example` para `.env`:
   ```bash
   cp .env.example .env
   ```
2. Abra o arquivo `.env` com qualquer editor de texto.
3. Insira sua chave gratuita do Google Gemini obtida no [Google AI Studio](https://aistudio.google.com):
   ```env
   GEMINI_API_KEY=AIzaSyD...sua_chave_real_aqui...
   ```
   *(Nota: O Secrets Gradle Plugin lerá automaticamente esse arquivo e injetará a chave em `BuildConfig.GEMINI_API_KEY` em tempo de compilação).*

4. *(Opcional)* Se for utilizar autenticação Google e nuvem Firestore:
   * Copie o arquivo oficial `google-services.json` do seu projeto Firebase para `app/google-services.json` (veja o modelo `app/google-services.json.template`).
   * Adicione `GOOGLE_WEB_CLIENT_ID=seu-id-web.apps.googleusercontent.com` no seu arquivo `.env`.
   * Caso não queira configurar o Firebase agora, não se preocupe: o projeto possui `MissingGoogleServicesStrategy.WARN` e opera perfeitamente no modo 100% offline com Room Database.

---

## 🔨 4. Compilação via Linha de Comando (CLI)

Se preferir utilizar o terminal:

### Para compilar o APK de depuração (Debug):
```bash
gradle assembleDebug
```
O arquivo gerado estará disponível em:
`app/build/outputs/apk/debug/app-debug.apk`

### Para rodar os testes unitários locais:
```bash
gradle :app:testDebugUnitTest
```

---

## 📱 5. Execução no Emulador ou Dispositivo Físico

### Via Android Studio:
1. Conecte um smartphone Android via cabo USB com a **Depuração USB** ativada nas Opções do Desenvolvedor, OU crie um Dispositivo Virtual (AVD) com Android 13 ou superior.
2. Na barra superior do Android Studio, certifique-se de que a configuração selecionada é **app**.
3. Clique no botão verde de **Run (Shift + F10)**.
4. O app será instalado e aberto automaticamente.

---

## ✅ 6. Checklist de Verificação da Primeira Execução

* [ ] O app abre exibindo a tela inicial do **Ajusta** com o card de boas-vindas do ateliê.
* [ ] Ao clicar no ícone de sol/lua no topo, o tema alterna suavemente entre modo claro e escuro.
* [ ] Ao abrir a tela "Analisar", a câmera ou seletor de fotos é solicitado com diálogo de permissão nativo.
* [ ] Ao realizar um diagnóstico com a chave `.env` configurada, a análise visual completa é carregada com a faixa de preços em R$.
