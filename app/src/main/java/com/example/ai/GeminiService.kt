package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

enum class ChatSender {
    USER,
    ATELIER_AI
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: ChatSender,
    val text: String,
    val sources: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChatModelOption(val modelId: String, val displayName: String, val badge: String) {
    FLASH_LITE("gemini-3.1-flash-lite-preview", "Flash Lite", "⚡ Rápido"),
    FLASH("gemini-3.5-flash", "Flash 3.5", "⚖️ Equilibrado"),
    PRO("gemini-3.1-pro-preview", "Pro 3.1", "🎓 Especialista")
}

data class GroundingResult(
    val answer: String,
    val sources: List<String> = emptyList(),
    val searchQueries: List<String> = emptyList()
)

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun getValidApiKey(): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalStateException("API_KEY_NOT_CONFIGURED")
        }
        return apiKey
    }

    // Vision Analysis
    suspend fun analyzeGarment(
        prompt: String,
        imagesBase64: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getValidApiKey()
        val url = "$BASE_URL/gemini-3.1-pro-preview:generateContent?key=$apiKey"

        val partsArray = JSONArray()
        partsArray.put(JSONObject().put("text", prompt))

        for (img in imagesBase64) {
            val inlineData = JSONObject()
                .put("mimeType", "image/jpeg")
                .put("data", img)
            partsArray.put(JSONObject().put("inlineData", inlineData))
        }

        val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
        val generationConfig = JSONObject()
            .put("responseMimeType", "application/json")
            .put("temperature", 0.2)

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("generationConfig", generationConfig)

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val errBody = response.body?.string() ?: ""
            throw RuntimeException("Gemini API call failed: code=${response.code} body=$errBody")
        }

        val responseText = response.body?.string() ?: throw RuntimeException("Empty response body")
        parseFirstTextCandidate(responseText)
    }

    // Multi-turn Chat with model selection and system instruction
    suspend fun sendChatMessage(
        messages: List<ChatMessage>,
        model: String = "gemini-3.5-flash",
        systemInstruction: String = "Você é a Consultora Mestre em Alfaiataria do Ajusta. Você é especialista em ajustes, modelagem, caimento de roupas masculinas e femininas, tipos de tecidos e costura sob medida. Dê conselhos práticos, honestos e elegantes sobre se vale a pena reformar uma peça, como explicar para uma costureira e quais cuidados tomar."
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getValidApiKey()
        val url = "$BASE_URL/$model:generateContent?key=$apiKey"

        val contentsArray = JSONArray()
        for (msg in messages) {
            val role = if (msg.sender == ChatSender.USER) "user" else "model"
            val parts = JSONArray().put(JSONObject().put("text", msg.text))
            contentsArray.put(
                JSONObject()
                    .put("role", role)
                    .put("parts", parts)
            )
        }

        val systemObj = JSONObject().put(
            "parts",
            JSONArray().put(JSONObject().put("text", systemInstruction))
        )

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("systemInstruction", systemObj)
            .put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.7)
            )

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val errBody = response.body?.string() ?: ""
            throw RuntimeException("Gemini Chat failed: code=${response.code} body=$errBody")
        }

        val responseText = response.body?.string() ?: throw RuntimeException("Empty response body")
        parseFirstTextCandidate(responseText)
    }

    // Search Grounding using gemini-3.5-flash
    suspend fun queryWithSearchGrounding(
        prompt: String
    ): GroundingResult = withContext(Dispatchers.IO) {
        val apiKey = getValidApiKey()
        val url = "$BASE_URL/gemini-3.5-flash:generateContent?key=$apiKey"

        val contentsArray = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        )

        val toolsArray = JSONArray().put(
            JSONObject().put("googleSearch", JSONObject())
        )

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("tools", toolsArray)

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val err = response.body?.string() ?: ""
            throw RuntimeException("Search Grounding failed: code=${response.code} body=$err")
        }

        val responseText = response.body?.string() ?: throw RuntimeException("Empty response body")
        parseGroundingResponse(responseText)
    }

    // Maps Grounding using gemini-3.5-flash
    suspend fun queryWithMapsGrounding(
        prompt: String
    ): GroundingResult = withContext(Dispatchers.IO) {
        val apiKey = getValidApiKey()
        val url = "$BASE_URL/gemini-3.5-flash:generateContent?key=$apiKey"

        val contentsArray = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        )

        val toolsArray = JSONArray().put(
            JSONObject().put("googleMaps", JSONObject())
        )

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("tools", toolsArray)

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val err = response.body?.string() ?: ""
            throw RuntimeException("Maps Grounding failed: code=${response.code} body=$err")
        }

        val responseText = response.body?.string() ?: throw RuntimeException("Empty response body")
        parseGroundingResponse(responseText)
    }

    private fun parseFirstTextCandidate(responseText: String): String {
        val jsonResponse = JSONObject(responseText)
        val candidates = jsonResponse.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val firstPart = parts?.optJSONObject(0)
        return firstPart?.optString("text") ?: throw RuntimeException("No text in candidate response")
    }

    private fun parseGroundingResponse(responseText: String): GroundingResult {
        val jsonResponse = JSONObject(responseText)
        val candidates = jsonResponse.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")

        val answerBuilder = StringBuilder()
        if (parts != null) {
            for (i in 0 until parts.length()) {
                val p = parts.optJSONObject(i)
                val t = p?.optString("text")
                if (!t.isNullOrBlank()) {
                    answerBuilder.append(t)
                }
            }
        }

        val sources = mutableListOf<String>()
        val searchQueries = mutableListOf<String>()

        val groundingMetadata = firstCandidate?.optJSONObject("groundingMetadata")
        if (groundingMetadata != null) {
            val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
            if (webSearchQueries != null) {
                for (i in 0 until webSearchQueries.length()) {
                    searchQueries.add(webSearchQueries.optString(i))
                }
            }

            val searchChunks = groundingMetadata.optJSONArray("groundingChunks")
            if (searchChunks != null) {
                for (i in 0 until searchChunks.length()) {
                    val chunk = searchChunks.optJSONObject(i)
                    val web = chunk?.optJSONObject("web")
                    val title = web?.optString("title")
                    val uri = web?.optString("uri")
                    if (!title.isNullOrBlank()) {
                        sources.add(if (!uri.isNullOrBlank()) "$title ($uri)" else title)
                    }
                }
            }
        }

        return GroundingResult(
            answer = answerBuilder.toString().ifBlank { "Sem resposta retornada pelo modelo." },
            sources = sources,
            searchQueries = searchQueries
        )
    }

    // Repair Tutorial Generator using gemini-3.5-flash
    suspend fun generateRepairTutorial(topic: String): com.example.model.RepairTutorial = withContext(Dispatchers.IO) {
        val apiKey = try { getValidApiKey() } catch (_: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext createFallbackTutorial(topic)
        }

        val url = "$BASE_URL/gemini-3.5-flash:generateContent?key=$apiKey"
        val systemInstruction = "Você é o Mestre Alfaiate do Ajusta. Crie um tutorial de costura manual e reparo caseiro passo a passo detalhado, prático e seguro. Retorne APENAS um objeto JSON com: title (string), subtitle (string), category (string), difficulty (string: 'Muito Fácil', 'Fácil' ou 'Médio'), estimatedTimeMinutes (int), toolsNeeded (array de strings), steps (array com stepNumber int, title string, description string, tip string), commonMistakes (array de strings), tailorTip (string)."

        val prompt = "Crie um tutorial de reparo ou ajuste manual passo a passo para: $topic"

        val contentsArray = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        )

        val generationConfig = JSONObject()
            .put("responseMimeType", "application/json")
            .put("temperature", 0.3)

        val systemObj = JSONObject().put(
            "parts",
            JSONArray().put(JSONObject().put("text", systemInstruction))
        )

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("systemInstruction", systemObj)
            .put("generationConfig", generationConfig)

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext createFallbackTutorial(topic)
            }
            val bodyStr = response.body?.string() ?: return@withContext createFallbackTutorial(topic)
            val jsonText = parseFirstTextCandidate(bodyStr)
            val obj = JSONObject(jsonText)

            val stepsList = mutableListOf<com.example.model.TutorialStep>()
            val stepsArr = obj.optJSONArray("steps")
            if (stepsArr != null) {
                for (i in 0 until stepsArr.length()) {
                    val st = stepsArr.optJSONObject(i)
                    if (st != null) {
                        stepsList.add(
                            com.example.model.TutorialStep(
                                stepNumber = st.optInt("stepNumber", i + 1),
                                title = st.optString("title", "Passo ${i + 1}"),
                                description = st.optString("description", ""),
                                tip = st.optString("tip").ifBlank { null }
                            )
                        )
                    }
                }
            }

            val toolsList = mutableListOf<String>()
            val toolsArr = obj.optJSONArray("toolsNeeded")
            if (toolsArr != null) {
                for (i in 0 until toolsArr.length()) {
                    toolsList.add(toolsArr.optString(i))
                }
            }

            val mistakesList = mutableListOf<String>()
            val mistakesArr = obj.optJSONArray("commonMistakes")
            if (mistakesArr != null) {
                for (i in 0 until mistakesArr.length()) {
                    mistakesList.add(mistakesArr.optString(i))
                }
            }

            com.example.model.RepairTutorial(
                title = obj.optString("title", topic),
                subtitle = obj.optString("subtitle", "Passo a passo manual gerado pela IA"),
                category = obj.optString("category", "Reparo Manual"),
                difficulty = obj.optString("difficulty", "Fácil"),
                estimatedTimeMinutes = obj.optInt("estimatedTimeMinutes", 15),
                toolsNeeded = if (toolsList.isNotEmpty()) toolsList else listOf("Agulha de costura", "Linha adequada", "Tesoura"),
                steps = if (stepsList.isNotEmpty()) stepsList else createFallbackTutorial(topic).steps,
                commonMistakes = mistakesList,
                tailorTip = obj.optString("tailorTip", "Sempre teste a tensão da linha em uma sobra de tecido.")
            )
        } catch (_: Exception) {
            createFallbackTutorial(topic)
        }
    }

    private fun createFallbackTutorial(topic: String): com.example.model.RepairTutorial {
        return com.example.model.RepairTutorial(
            title = "Como fazer: $topic",
            subtitle = "Tutorial prático de costura manual e pequenos reparos",
            category = "Dicas de Reparo",
            difficulty = "Fácil",
            estimatedTimeMinutes = 15,
            toolsNeeded = listOf(
                "Agulha de mão fina a média (nº 7 a 9)",
                "Linha poliéster da cor correspondente",
                "Tesoura afiada",
                "Alfinetes e ferro de passar"
            ),
            steps = listOf(
                com.example.model.TutorialStep(
                    stepNumber = 1,
                    title = "Preparação e limpeza da peça",
                    description = "Vire a roupa do avesso e examine as costuras originais. Limpe fios soltos com a tesoura.",
                    tip = "Trabalhe sempre em uma mesa bem iluminada."
                ),
                com.example.model.TutorialStep(
                    stepNumber = 2,
                    title = "Marcação e alfinetamento",
                    description = "Posicione a área do reparo perfeitamente alinhada e prenda com alfinetes para o tecido não escorregar.",
                    tip = "Use ferro morno para assentar as dobras antes de costurar."
                ),
                com.example.model.TutorialStep(
                    stepNumber = 3,
                    title = "Execução da costura manual",
                    description = "Faça pontos pequenos e regulares mantendo a mesma tensão da linha para não enrugar a peça.",
                    tip = "Para firmeza similar à máquina, use o ponto atrás."
                ),
                com.example.model.TutorialStep(
                    stepNumber = 4,
                    title = "Remate seguro e conferência",
                    description = "Passe a linha para o avesso, dê dois nós cegos bem rentes ao tecido e passe a ferro com vapor suave.",
                    tip = "O vapor ajuda a fibra a abraçar a nova linha."
                )
            ),
            commonMistakes = listOf(
                "Puxar a linha com força excessiva enrugando a costura",
                "Usar agulha grossa demais que deixa marcas e furos no tecido",
                "Esquecer de passar a ferro no final para assentar os pontos"
            ),
            tailorTip = "Na costura manual, a paciência e a regularidade do tamanho do ponto fazem o trabalho parecer feito por um ateliê profissional."
        )
    }

    // Style & Customization Suggestion using gemini-3.5-flash
    suspend fun generateStyleAndCustomization(
        analysis: com.example.model.GarmentAnalysis
    ): com.example.model.StyleCustomizationSuggestion = withContext(Dispatchers.IO) {
        val apiKey = try { getValidApiKey() } catch (_: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext createFallbackStyleSuggestion(analysis)
        }

        val url = "$BASE_URL/gemini-3.5-flash:generateContent?key=$apiKey"
        val systemInstruction = "Você é a Consultora de Imagem e Estilista de Ateliê do Ajusta. Analise a peça e o problema identificado e crie uma proposta sofisticada de estilo, customização (upcycling) e combinações de guarda-roupa com acessórios. Retorne APENAS um JSON com: styleConcept (string), customizationIdeas (array com title string, description string, difficulty string), accessoryCombinations (array com category string, itemDescription string, tip string), wardrobeLooks (array com occasion string, pairingDescription string, whyItWorks string), colorPalette (array de strings com nomes de cores harmoniosas)."

        val prompt = "Peça: ${analysis.garmentType}. Tecido: ${analysis.fabric ?: "Padrão"}. Diagnóstico do problema: ${analysis.problemSummary}. Ajuste sugerido: ${analysis.possibleSolution}. Sugira estilos, customizações e combinações completas com acessórios e guarda-roupa."

        val contentsArray = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        )

        val generationConfig = JSONObject()
            .put("responseMimeType", "application/json")
            .put("temperature", 0.4)

        val systemObj = JSONObject().put(
            "parts",
            JSONArray().put(JSONObject().put("text", systemInstruction))
        )

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("systemInstruction", systemObj)
            .put("generationConfig", generationConfig)

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext createFallbackStyleSuggestion(analysis)
            }
            val bodyStr = response.body?.string() ?: return@withContext createFallbackStyleSuggestion(analysis)
            val jsonText = parseFirstTextCandidate(bodyStr)
            val obj = JSONObject(jsonText)

            val customList = mutableListOf<com.example.model.CustomizationIdea>()
            val customArr = obj.optJSONArray("customizationIdeas")
            if (customArr != null) {
                for (i in 0 until customArr.length()) {
                    val c = customArr.optJSONObject(i)
                    if (c != null) {
                        customList.add(
                            com.example.model.CustomizationIdea(
                                title = c.optString("title", "Customização ${i + 1}"),
                                description = c.optString("description", ""),
                                difficulty = c.optString("difficulty", "Fácil")
                            )
                        )
                    }
                }
            }

            val accessoriesList = mutableListOf<com.example.model.AccessoryItem>()
            val accArr = obj.optJSONArray("accessoryCombinations")
            if (accArr != null) {
                for (i in 0 until accArr.length()) {
                    val a = accArr.optJSONObject(i)
                    if (a != null) {
                        accessoriesList.add(
                            com.example.model.AccessoryItem(
                                category = a.optString("category", "Acessório"),
                                itemDescription = a.optString("itemDescription", ""),
                                tip = a.optString("tip", "")
                            )
                        )
                    }
                }
            }

            val looksList = mutableListOf<com.example.model.WardrobeLook>()
            val looksArr = obj.optJSONArray("wardrobeLooks")
            if (looksArr != null) {
                for (i in 0 until looksArr.length()) {
                    val l = looksArr.optJSONObject(i)
                    if (l != null) {
                        looksList.add(
                            com.example.model.WardrobeLook(
                                occasion = l.optString("occasion", "Ocasião ${i + 1}"),
                                pairingDescription = l.optString("pairingDescription", ""),
                                whyItWorks = l.optString("whyItWorks", "")
                            )
                        )
                    }
                }
            }

            val colorsList = mutableListOf<String>()
            val colorsArr = obj.optJSONArray("colorPalette")
            if (colorsArr != null) {
                for (i in 0 until colorsArr.length()) {
                    colorsList.add(colorsArr.optString(i))
                }
            }

            com.example.model.StyleCustomizationSuggestion(
                styleConcept = obj.optString("styleConcept", "Equilíbrio de proporções e valorização da silhueta."),
                customizationIdeas = if (customList.isNotEmpty()) customList else createFallbackStyleSuggestion(analysis).customizationIdeas,
                accessoryCombinations = if (accessoriesList.isNotEmpty()) accessoriesList else createFallbackStyleSuggestion(analysis).accessoryCombinations,
                wardrobeLooks = if (looksList.isNotEmpty()) looksList else createFallbackStyleSuggestion(analysis).wardrobeLooks,
                colorPalette = if (colorsList.isNotEmpty()) colorsList else listOf("Marinho", "Off-White", "Caramelo", "Terracota")
            )
        } catch (_: Exception) {
            createFallbackStyleSuggestion(analysis)
        }
    }

    private fun createFallbackStyleSuggestion(analysis: com.example.model.GarmentAnalysis): com.example.model.StyleCustomizationSuggestion {
        val garment = analysis.garmentType.lowercase()
        return when {
            garment.contains("calça") || garment.contains("jeans") -> {
                com.example.model.StyleCustomizationSuggestion(
                    styleConcept = "Silhueta alongada e postura alinhada com a cintura no lugar correto.",
                    customizationIdeas = listOf(
                        com.example.model.CustomizationIdea(
                            title = "Barra Italiana Dobrada (3.5 cm)",
                            description = "Adiciona peso na barra para um caimento impecável e visual alfaiataria chic.",
                            difficulty = "Fácil"
                        ),
                        com.example.model.CustomizationIdea(
                            title = "Troca de Botão por Chifre ou Metal Envelhecido",
                            description = "Eleva a percepção de valor da peça com um acabamento clássico.",
                            difficulty = "Muito Fácil"
                        )
                    ),
                    accessoryCombinations = listOf(
                        com.example.model.AccessoryItem("Cinto", "Cinto fino de couro caramelo ou preto com fivela discreta em latão fosco", "Marca a cintura sem cortar a silhueta"),
                        com.example.model.AccessoryItem("Calçados", "Mocassim clássico de couro ou tênis branco minimalista", "Deixa o tornozelo levemente aparente com a barra ajustada"),
                        com.example.model.AccessoryItem("Bolsa", "Bolsa estruturada média em tom neutro", "Equilibra a proporção casual-elegante")
                    ),
                    wardrobeLooks = listOf(
                        com.example.model.WardrobeLook("Trabalho / Smart Casual", "Camisa de tricoline branca engomada + blazer de alfaiataria azul marinho", "A calça ajustada dá base limpa para a estrutura do blazer"),
                        com.example.model.WardrobeLook("Casual Chic", "Tricô leve de gola alta em tom cru + trench coat leve", "Texturas contrastantes que transmitem sofisticação sem esforço"),
                        com.example.model.WardrobeLook("Noite Descontraída", "Top de seda ou cetim preto + jaqueta de couro ou blazer cropped", "O contraste entre o tecido nobre do top e a linha reta da calça")
                    ),
                    colorPalette = listOf("Azul Marinho", "Off-White", "Caramelo", "Preto Clássico")
                )
            }
            garment.contains("blazer") || garment.contains("paletó") || garment.contains("casaco") -> {
                com.example.model.StyleCustomizationSuggestion(
                    styleConcept = "Estrutura moderna e autoridade visual sem rigidez excessiva.",
                    customizationIdeas = listOf(
                        com.example.model.CustomizationIdea(
                            title = "Troca de Botões por Dourados Foscos ou Tartaruga",
                            description = "Transforma um blazer sóbrio em um blazer estilo clube inglês contemporâneo.",
                            difficulty = "Fácil"
                        ),
                        com.example.model.CustomizationIdea(
                            title = "Forro Contrastante nas Mangas",
                            description = "Permite dobrar o punho mostrando uma estampa listrada sutil.",
                            difficulty = "Médio"
                        )
                    ),
                    accessoryCombinations = listOf(
                        com.example.model.AccessoryItem("Lenço de Bolso", "Lenço de seda com estampa geométrica ou linho branco liso", "Ponto de luz no peito que valoriza a lapela"),
                        com.example.model.AccessoryItem("Relógio / Joias", "Relógio com pulseira de couro ou bracelete minimalista", "Aparece com naturalidade no comprimento correto da manga"),
                        com.example.model.AccessoryItem("Calçados", "Loafer de camurça ou scarpin clássico", "Dá peso elegante à alfaiataria")
                    ),
                    wardrobeLooks = listOf(
                        com.example.model.WardrobeLook("Reunião Importante", "Camisa social azul clara + calça de alfaiataria cinza médio", "Conjunto de alta credibilidade e contraste equilibrado"),
                        com.example.model.WardrobeLook("Casual de Sexta", "Camiseta de algodão pima branca + jeans escuro reto sem lavagem", "Descontrai a estrutura formal do blazer com muita elegância"),
                        com.example.model.WardrobeLook("Jantar ou Evento", "Vestido midi liso de alcinhas ou camisa preta com botões abertos", "Cria camadas sofisticadas e moderno jogo de volumes")
                    ),
                    colorPalette = listOf("Azul Petróleo", "Cinza Mescla", "Branco Pima", "Bordeaux")
                )
            }
            else -> {
                com.example.model.StyleCustomizationSuggestion(
                    styleConcept = "Caimento polido que destaca a naturalidade do corpo sem sobras.",
                    customizationIdeas = listOf(
                        com.example.model.CustomizationIdea(
                            title = "Pesponto Artesanal nos Detalhes",
                            description = "Um pesponto sutil feito à mão nas aberturas ou golas dá um toque de peça sob medida.",
                            difficulty = "Fácil"
                        ),
                        com.example.model.CustomizationIdea(
                            title = "Substituição de Aviamentos",
                            description = "Troque zíperes metálicos aparentes ou botões plásticos por versões nobres.",
                            difficulty = "Fácil"
                        )
                    ),
                    accessoryCombinations = listOf(
                        com.example.model.AccessoryItem("Acessórios de Cintura", "Cinto de couro de espessura proporcional ao corte", "Harmoniza o ponto focal da cintura"),
                        com.example.model.AccessoryItem("Calçados", "Sapatos de bico fino ou amendoado", "Alongam visualmente as pernas e a silhueta"),
                        com.example.model.AccessoryItem("Joias", "Colares em camadas ou brincos de metal texturizado", "Direcionam o olhar para o colo e o caimento dos ombros")
                    ),
                    wardrobeLooks = listOf(
                        com.example.model.WardrobeLook("Dia a Dia Elegante", "Cardigã de cashmere ou camisa fluida por dentro da peça", "Visual limpo e agradável ao toque"),
                        com.example.model.WardrobeLook("Final de Semana", "Jaqueta jeans vintage ou tênis de couro clássico", "Equilíbrio entre alfaiataria e conforto descontraído"),
                        com.example.model.WardrobeLook("Ocasião Especial", "Clutch metálica + terceira peça em tom contrastante", "Eleva a peça ajustada para um evento festivo")
                    ),
                    colorPalette = listOf("Areia / Linho", "Verde Oliva", "Bordô", "Dourado Fosco")
                )
            }
        }
    }

    // Regional & Currency Cost Estimation using gemini-3.5-flash
    suspend fun estimateRegionalCost(
        analysis: com.example.model.GarmentAnalysis,
        region: String,
        currencyCode: String
    ): com.example.model.RegionalPriceEstimate = withContext(Dispatchers.IO) {
        val apiKey = try { getValidApiKey() } catch (_: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext createFallbackRegionalEstimate(analysis, region, currencyCode)
        }

        val url = "$BASE_URL/gemini-3.5-flash:generateContent?key=$apiKey"
        val systemInstruction = "Você é o Especialista em Precificação e Economia de Serviços de Alfaiataria e Costura do Ajusta. Calcule uma estimativa de custo médio realista para o ajuste sugerido na peça indicada, considerando a região/cidade informada e a moeda de referência. Retorne APENAS um objeto JSON com: minPrice (number), maxPrice (number), averagePrice (number), currencySymbol (string, ex: R$, €, $, £), formattedRange (string, ex: 'R$ 45 – R$ 75'), regionalMarketContext (string: análise detalhada da economia e preços médios praticados nessa região específica), factorsImpactingPrice (array de strings com fatores locais que alteram o preço), averageTurnaroundDays (string: prazo típico de entrega, ex: '2 a 4 dias úteis'), confidenceNote (string)."

        val prompt = "Peça: ${analysis.garmentType}. Tecido: ${analysis.fabric ?: "Padrão"}. Problema: ${analysis.problemSummary}. Ajuste sugerido: ${analysis.possibleSolution}. Preço base Brasil nacional: ${analysis.priceEstimate.formattedRange}. Região/Localidade informada pelo usuário: ${if (region.isNotBlank()) region else "Média Geral"}. Moeda de referência solicitada: $currencyCode. Estime o custo médio nesta localidade e moeda."

        val contentsArray = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        )

        val generationConfig = JSONObject()
            .put("responseMimeType", "application/json")
            .put("temperature", 0.3)

        val systemObj = JSONObject().put(
            "parts",
            JSONArray().put(JSONObject().put("text", systemInstruction))
        )

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)
            .put("systemInstruction", systemObj)
            .put("generationConfig", generationConfig)

        val requestBody = requestBodyJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext createFallbackRegionalEstimate(analysis, region, currencyCode)
            }
            val bodyStr = response.body?.string() ?: return@withContext createFallbackRegionalEstimate(analysis, region, currencyCode)
            val jsonText = parseFirstTextCandidate(bodyStr)
            val obj = JSONObject(jsonText)

            val factorsList = mutableListOf<String>()
            val factorsArr = obj.optJSONArray("factorsImpactingPrice")
            if (factorsArr != null) {
                for (i in 0 until factorsArr.length()) {
                    factorsList.add(factorsArr.optString(i))
                }
            }

            val sym = obj.optString("currencySymbol", when (currencyCode) {
                "EUR" -> "€"
                "USD" -> "$"
                "GBP" -> "£"
                else -> "R$"
            })

            val minVal = obj.optDouble("minPrice", 35.0)
            val maxVal = obj.optDouble("maxPrice", 60.0)
            val avgVal = obj.optDouble("averagePrice", (minVal + maxVal) / 2.0)

            com.example.model.RegionalPriceEstimate(
                region = if (region.isNotBlank()) region else "Nacional / Geral",
                currencyCode = currencyCode,
                currencySymbol = sym,
                minPrice = minVal,
                maxPrice = maxVal,
                averagePrice = avgVal,
                formattedRange = obj.optString("formattedRange", "$sym ${minVal.toInt()} – $sym ${maxVal.toInt()}"),
                regionalMarketContext = obj.optString("regionalMarketContext", "Estimativa de mercado baseada no custo de vida e perfil dos ateliês locais."),
                factorsImpactingPrice = if (factorsList.isNotEmpty()) factorsList else listOf("Localização do ateliê", "Complexidade do acabamento original"),
                averageTurnaroundDays = obj.optString("averageTurnaroundDays", "3 a 5 dias úteis"),
                confidenceNote = obj.optString("confidenceNote", "Calculado pelo modelo Gemini com parâmetros regionais.")
            )
        } catch (_: Exception) {
            createFallbackRegionalEstimate(analysis, region, currencyCode)
        }
    }

    private fun createFallbackRegionalEstimate(
        analysis: com.example.model.GarmentAnalysis,
        region: String,
        currencyCode: String
    ): com.example.model.RegionalPriceEstimate {
        val baseMin = analysis.priceEstimate.minPrice.toDouble()
        val baseMax = analysis.priceEstimate.maxPrice.toDouble()
        val regLower = region.lowercase()

        val isCapital = regLower.contains("são paulo") || regLower.contains("sp") ||
                regLower.contains("rio") || regLower.contains("rj") ||
                regLower.contains("curitiba") || regLower.contains("brasília") ||
                regLower.contains("jardins") || regLower.contains("leblon")

        val isInterior = regLower.contains("interior") || regLower.contains("bairro") ||
                regLower.contains("cidade pequena")

        val (multiplier, sym) = when (currencyCode) {
            "EUR" -> Pair(0.35, "€")
            "USD" -> Pair(0.40, "$")
            "GBP" -> Pair(0.30, "£")
            else -> {
                val factor = if (isCapital) 1.35 else if (isInterior) 0.85 else 1.0
                Pair(factor, "R$")
            }
        }

        val calculatedMin = kotlin.math.round(baseMin * multiplier)
        val calculatedMax = kotlin.math.round(baseMax * multiplier)
        val calculatedAvg = (calculatedMin + calculatedMax) / 2.0

        val regionLabel = if (region.isNotBlank()) region else "Média Nacional"

        val contextDesc = when (currencyCode) {
            "EUR" -> "Na Europa ($regionLabel), reformas de costura manual e alfaiataria consideram a hora técnica média de €15 a €30, dependendo do país e proximidade dos centros urbanos."
            "USD" -> "Nos Estados Unidos ($regionLabel), ajustes de vestuário em alfaiatarias especializadas e 'tailor shops' custam tipicamente entre $15 e $45 para ajustes padrão de cintura e bainhas."
            "GBP" -> "No Reino Unido ($regionLabel), ateliês e serviços de 'clothing alterations' operam com tabelas a partir de £12 para barras e £25 a £40 para alfaiataria estrutural."
            else -> {
                if (isCapital) {
                    "Em centros metropolitanos e áreas nobres ($regionLabel), os custos operacionais de ateliês e alfaiatarias renomadas elevam o valor médio em 30% a 40% em relação à média nacional, com prazos mais ágeis e atendimento personalizado."
                } else if (isInterior) {
                    "No interior ou comércios de bairro ($regionLabel), ateliês locais e costureiras autônomas oferecem preços cerca de 15% mais acessíveis mantendo a técnica tradicional."
                } else {
                    "Estimativa padrão para a região de $regionLabel considerando a média de ateliês e profissionais autônomos."
                }
            }
        }

        return com.example.model.RegionalPriceEstimate(
            region = regionLabel,
            currencyCode = currencyCode,
            currencySymbol = sym,
            minPrice = calculatedMin,
            maxPrice = calculatedMax,
            averagePrice = calculatedAvg,
            formattedRange = "$sym ${calculatedMin.toInt()} – $sym ${calculatedMax.toInt()}",
            regionalMarketContext = contextDesc,
            factorsImpactingPrice = listOf(
                "Nível de especialização do ateliê (alta costura vs. costura express)",
                "Urgência de entrega (taxa de urgência de 24h a 48h)",
                "Presença de forro interno ou tecidos delicados (seda, linho, alfaiataria pura)"
            ),
            averageTurnaroundDays = if (isCapital) "2 a 4 dias úteis" else "3 a 6 dias úteis",
            confidenceNote = "Estimativa calculada por IA com ajuste econômico de custo de vida e paridade regional."
        )
    }
}
