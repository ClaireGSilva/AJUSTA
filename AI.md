# AI — Documentação de Inteligência Artificial — Ajusta

> **Documentação Técnica do Subsistema de IA Generativa e Visão Computacional**  
> *Versão:* 1.0.0 | *Provedor Primário:* Google DeepMind / Google Gemini API

---

## 🤖 1. Recursos do Aplicativo que Utilizam IA

O **Ajusta** integra Inteligência Artificial Generativa em 4 frentes centrais:

1. **Diagnóstico Visual Multimodal de Roupas:**
   * Analisa imagens capturadas pela câmera ou galeria.
   * Identifica a peça, o tipo de tecido aparente, a avaria estrutural ou defeito de caimento e gera um veredito técnico estruturado em formato JSON rigoroso.
2. **Consultora Mestre em Alfaiataria (Chat Conversacional):**
   * Interface de chat multi-turn com persona técnica e acolhedora especializada em alfaiataria clássica, reformas, caimento e tecidos.
3. **Planejador de Procedimento de Ajuste (Alteration Planner):**
   * Mapeamento dos passos práticos que um profissional ou usuário DIY precisará realizar para consertar a peça.
4. **Detecção e Avaliação de Riscos Têxteis:**
   * Identificação de fragilidades do tecido (ex: marcas permanentes de agulha em cetim/couro, perda de proporção de bolsos, fios puxados).

---

## 🎯 2. Modelos e Provedores Utilizados

Os modelos são consumidos através da **Google Gemini API (Generative Language v1beta)**:

| Recurso | Modelo Utilizado | Justificativa Técnica |
|---|---|---|
| **Visão Computacional Multimodal** | `gemini-3.1-pro-preview` | Máxima capacidade de raciocínio espacial e visual para inspecionar costuras, bainhas e tecidos. |
| **Chat: Modo Flash 3.5 (Padrão)** | `gemini-3.5-flash` | Equilíbrio perfeito entre velocidade, empatia conversacional e baixo custo. |
| **Chat: Modo Flash Lite** | `gemini-3.1-flash-lite-preview` | Respostas de baixíssima latência para conexões móveis lentas. |
| **Chat: Modo Pro Especialista** | `gemini-3.1-pro-preview` | Análises aprofundadas para peças complexas de alfaiataria (ternos, vestidos de noiva). |

---

## 📝 3. Prompts Principais e Engenharia de Prompt

### 3.1 Prompt de Extração Multimodal (Garment Analysis)
* **Localização no Código:** `com.example.ai.GarmentVisionAnalyzer`
* **Instrução Central:**
```text
Você é um Alfaiate Mestre e Perito Têxtil de Alta Costura.
Analise a vestimenta e o problema visual presente nas fotos fornecidas.

Responda OBRIGATORIAMENTE em formato JSON válido contendo exatamente as seguintes chaves:
- garmentType: Tipo exato da peça (ex: Calça Social, Blazer, Camisa, Vestido)
- visibleCharacteristics: Características físicas visíveis da peça
- problemSummary: Resumo do problema ou ajuste necessário
- problemCategory: Categoria do ajuste (ex: Bainha, Ajuste de Cintura, Troca de Zíper, Manga, Ombreira)
- confidence: ALTA, MEDIA ou BAIXA
- whatWeObserved: Descrição técnica do que foi observado na imagem
- probableCause: Causa provável do problema
- possibleSolution: Procedimento de costura sugerido
- whatNeedsConfirmation: O que precisa ser confirmado presencialmente no corpo do cliente
- beforeYouAlterLimitations: Limitações prévias da peça antes de cortar ou alterar
- fabric: Tecido identificado ou provável
- riskLevel: BAIXO, MEDIO ou ALTO
- riskSummary: Explicação do risco envolvido
- specificRisks: Array de strings com riscos pontuais
- requiresInPersonEvaluation: Booleano indicando se prova física é indispensável
- suggestedBasePrice: Valor numérico em R$ sugerido para mão de obra
- complexityLevel: FACIL, MEDIA ou DIFICIL
```

### 3.2 System Instruction do Chat (Consultora Mestre)
* **Localização no Código:** `com.example.ai.GeminiService.sendChatMessage`
* **Prompt de Sistema:**
```text
Você é a Consultora Mestre em Alfaiataria do Ajusta. Você é especialista em ajustes, 
modelagem, caimento de roupas masculinas e femininas, tipos de tecidos e costura sob medida. 
Dê conselhos práticos, honestos e elegantes sobre se vale a pena reformar uma peça, 
como explicar para uma costureira e quais cuidados tomar. Mantenha um tom profissional, 
acolhedor e didático em Português do Brasil.
```

---

## ⚙️ 4. Variáveis de Ambiente Necessárias

| Variável | Onde Configurar | Descrição |
|---|---|---|
| `GEMINI_API_KEY` | Arquivo `.env` na raiz do projeto | Chave de autenticação obtida no Google AI Studio (https://aistudio.google.com). Injetada em tempo de compilação no `BuildConfig.GEMINI_API_KEY` pelo Secrets Gradle Plugin. |

---

## 💰 5. Estimativa de Custos Operacionais

A API do Gemini possui uma das estruturas de preço mais acessíveis do mercado de IA:

* **No Tier Gratuito do Google AI Studio:**
  * Até 15 requisições por minuto (RPM) gratuitas para prototipagem e desenvolvimento.
* **No Tier Pago (Pay-as-you-go):**
  * **Gemini 3.5 Flash / Flash Lite:** ~US$ 0,10 a US$ 0,15 por 1 milhão de tokens de entrada.
  * **Gemini Pro (com imagem):** ~US$ 1,25 a US$ 2,50 por 1 milhão de tokens.
  * **Custo Médio por Diagnóstico Completo:** Menos de **US$ 0,001 (uma fração de centavo de real)** por análise realizada.

---

## ⚠️ 6. Limitações Conhecidas da IA

1. **Iluminação e Foco:** Fotos escuras, com sombras fortes ou desfocadas podem levar a classificações imprecisas da textura do tecido.
2. **Medições Milimétricas:** A IA não substitui uma fita métrica no corpo humano. Por isso, toda análise inclui o campo obrigatório `whatNeedsConfirmation` e alerta o usuário sobre a necessidade da prova física.
3. **Composição Fibrilar Exata:** Não é possível diferenciar 100% linho puro de linho com viscose apenas por imagem sem teste de queima ou etiqueta física.

---

## 🔄 7. Como Substituir o Provedor ou Modelo de IA

A arquitetura do **Ajusta** foi desenhada para facilitar a substituição de provedores caso o novo proprietário prefira utilizar OpenAI (GPT-4o), Anthropic (Claude 3.5 Sonnet) ou modelos locais:

1. **Camada Desacoplada:** Todas as chamadas de IA estão concentradas em `com.example.ai.GeminiService.kt`.
2. **Para trocar o modelo Gemini:**
   * Altere as constantes ou adicione novas opções no enum `ChatModelOption` em `GeminiService.kt`.
3. **Para migrar para outro provedor (ex: OpenAI):**
   * Substitua a URL base em `GeminiService.kt` para `https://api.openai.com/v1/chat/completions`.
   * Ajuste o mapeamento do corpo da requisição (`messages` em vez de `contents/parts`).
   * Como a saída exigida pelo `GarmentVisionAnalyzer` é JSON puro, nenhum código das telas ou ViewModels precisará ser alterado.
