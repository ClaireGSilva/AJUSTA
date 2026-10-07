# API — Ajusta

> **Documentação de Integrações, Endpoints e Contratos de Rede**  
> *Versão:* 1.0.0 | *Padrão:* REST / JSON

---

## 🌐 Visão Geral de Comunicação Externa

O **Ajusta** comunica-se diretamente com a API oficial do **Google Generative AI (Gemini)** via HTTP/1.1 e HTTP/2 utilizando **OkHttp 4.10** com headers autenticados por chave de API.

---

## 1. Google Gemini Generative Language API

* **Base URL:** `https://generativelanguage.googleapis.com/v1beta/models`
* **Método de Autenticação:** Query parameter `?key={GEMINI_API_KEY}`
* **Headers:** `Content-Type: application/json; charset=utf-8`

### Endpoints Utilizados:

#### 1.1 Análise Multimodal de Vestuário (Visão Computacional)
* **Endpoint:** `POST /gemini-3.1-pro-preview:generateContent?key={GEMINI_API_KEY}`
* **Payload:**
```json
{
  "contents": [
    {
      "parts": [
        {
          "text": "Analise a vestimenta e o defeito estrutural... [Prompt completo de extração JSON]"
        },
        {
          "inlineData": {
            "mimeType": "image/jpeg",
            "data": "<BASE_64_IMAGE_DATA_1>"
          }
        },
        {
          "inlineData": {
            "mimeType": "image/jpeg",
            "data": "<BASE_64_IMAGE_DATA_2>"
          }
        }
      ]
    }
  ],
  "generationConfig": {
    "responseMimeType": "application/json",
    "temperature": 0.2
  }
}
```

* **Estrutura de Resposta Esperada (JSON estrito):**
```json
{
  "garmentType": "Calça Alfaiataria",
  "visibleCharacteristics": "Cós estruturado, passantes duplos, bolso faca",
  "problemSummary": "Cós folgado na região lombar traseira",
  "problemCategory": "Ajuste de Cintura",
  "confidence": "ALTA",
  "whatWeObserved": "Excesso de aproximadamente 4cm de tecido no centro das costas",
  "probableCause": "Modelagem original desproporcional ao corpo do usuário",
  "possibleSolution": "Abertura do cós traseiro, ajuste na costura central e rebate",
  "whatNeedsConfirmation": "Verificar se a sobra de tecido afeta o posicionamento dos bolsos traseiros",
  "beforeYouAlterLimitations": "Presença de forro interno ou costura embutida",
  "fabric": "Linho Misto com Viscose",
  "riskLevel": "BAIXO",
  "riskSummary": "Ajuste padrão de alfaiataria com ampla margem de segurança",
  "specificRisks": [
    "Possível marcação de furo de costura se o tecido já tiver sido vincado"
  ],
  "requiresInPersonEvaluation": true,
  "suggestedBasePrice": 45.0,
  "complexityLevel": "MEDIA"
}
```

---

#### 1.2 Chat com a Consultora de Alfaiataria
* **Modelos Suportados:**
  * `gemini-3.1-flash-lite-preview`
  * `gemini-3.5-flash`
  * `gemini-3.1-pro-preview`
* **Endpoint:** `POST /{model}:generateContent?key={GEMINI_API_KEY}`
* **Payload:**
```json
{
  "contents": [
    {
      "role": "user",
      "parts": [{ "text": "Minha calça jeans rasgou perto do zíper. Tem conserto?" }]
    },
    {
      "role": "model",
      "parts": [{ "text": "Olá! Rasgos próximos ao zíper exigem atenção especial..." }]
    },
    {
      "role": "user",
      "parts": [{ "text": "Qual técnica a costureira costuma usar nesse caso?" }]
    }
  ],
  "systemInstruction": {
    "parts": [
      {
        "text": "Você é a Consultora Mestre em Alfaiataria do Ajusta. Você é especialista em ajustes, modelagem, caimento de roupas masculinas e femininas, tipos de tecidos e costura sob medida..."
      }
    ]
  },
  "generationConfig": {
    "temperature": 0.7
  }
}
```

---

## 2. APIs Externas Não Utilizadas / Futuras

Para preservar a transferibilidade estrita e o princípio da verdade (não inventar implementações):
* **Geolocalização / Google Maps API:** `TODO / NOT YET DEFINED` (O catálogo de ateliês atual opera com registros estáticos locais indexados por cidade e especialidade; mapas ao vivo podem ser ativados pelo comprador se desejar).
* **Gateway de Pagamento (Stripe / Mercado Pago):** `TODO / NOT YET DEFINED` (A precificação atual é um motor de cálculo consultivo para o usuário e geração de ordem de serviço).
* **Push Notifications Remotas (FCM):** `TODO / NOT YET DEFINED` (O sistema atual usa `AlarmManager` local do Android, que não depende de servidor push nem consome custos operacionais).
