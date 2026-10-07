# CONFIGURATION — Ajusta

> **Guia de Configuração, Customização, Branding e Variáveis de Ambiente**  
> *Versão:* 1.0.0 | *Padrão:* Separação Estrita de Código e Configuração

---

## 🔒 1. Gestão de Segredos e Chaves de API

O **Ajusta** utiliza o **Secrets Gradle Plugin** (`com.google.android.libraries.mapsplatform.secrets-gradle-plugin`), garantindo que nenhuma chave de API fique exposta em repositórios Git públicos.

### 1.1 Arquivo `.env` (Raiz do Projeto)
O arquivo `.env` é ignorado pelo Git (definido no `.gitignore`) e serve para injetar as credenciais no momento do build:

```env
# Chave da API Google Gemini (Obrigatória para IA e Visão)
GEMINI_API_KEY=AIzaSy...sua_chave...
```

### 1.2 Acesso em Código Kotlin
No código, o segredo é acessado de forma fortemente tipada:
```kotlin
val apiKey = BuildConfig.GEMINI_API_KEY
```

Se a chave estiver vazia ou for igual ao placeholder padrão (`MY_GEMINI_API_KEY`), o serviço `GeminiService` lança uma exceção informativa alertando o desenvolvedor para configurar o `.env`.

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

## 🔥 3. Configuração do Firebase (Opcional)

O aplicativo foi projetado para não depender obrigatoriamente do Firebase para funcionar. No entanto, para ativar login Google e sincronização em nuvem:

1. Acesse o [Firebase Console](https://console.firebase.google.com).
2. Crie um projeto Firebase.
3. Adicione um aplicativo Android informando seu `applicationId`.
4. Baixe o arquivo `google-services.json`.
5. Coloque o arquivo em `app/google-services.json`.
6. No console do Firebase:
   * Em **Authentication**, ative o provedor **Google**.
   * Em **Firestore Database**, crie um banco de dados no modo de teste ou configure as regras de segurança padrão documentadas em [DATABASE.md](./DATABASE.md).
