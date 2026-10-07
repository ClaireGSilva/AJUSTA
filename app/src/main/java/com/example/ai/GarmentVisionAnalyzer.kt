package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.model.AlterationOption
import com.example.model.AlterationPlan
import com.example.model.ComplexityLevel
import com.example.model.ConfidenceLevel
import com.example.model.DiagnosticResult
import com.example.model.DiagnosticStatus
import com.example.model.GarmentAnalysis
import com.example.model.RiskAssessment
import com.example.model.RiskLevel
import com.example.pricing.PriceEstimateEngine
import com.example.pricing.PricingContext
import org.json.JSONObject
import java.util.UUID

/**
 * Camada GarmentVisionAnalyzer:
 * Responsável por conectar com a API multimodal do Gemini, processar imagens e descrições
 * textuais do usuário, e estruturar o resultado final nos modelos formais GarmentAnalysis
 * e DiagnosticResult conforme a arquitetura proposta.
 */
object GarmentVisionAnalyzer {
    private const val TAG = "GarmentVisionAnalyzer"

    suspend fun analyze(
        bitmaps: List<Bitmap>,
        photoUris: List<String> = emptyList(),
        problemCategory: String,
        problemDescription: String,
        fabric: String? = null,
        pricePaid: Double? = null
    ): DiagnosticResult {
        val startTime = System.currentTimeMillis()
        val base64Images = bitmaps.map { GeminiService.bitmapToBase64(it) }

        // Fallback imediato se não houver imagens ou API key configurada
        if (base64Images.isEmpty()) {
            val fallbackAnalysis = DiagnosticEngine.generateDeterministicAnalysis(
                garmentName = "Peça de vestuário",
                problemDescription = problemDescription,
                problemCategory = problemCategory,
                fabric = fabric,
                pricePaid = pricePaid,
                photoUris = photoUris
            )
            val primaryAlteration = fallbackAnalysis.alterationOptions.firstOrNull()
            val plan = primaryAlteration?.let {
                AlterationPlan(primaryAlteration = it, alternativeAlterations = fallbackAnalysis.alterationOptions.drop(1))
            }
            return DiagnosticResult(
                id = UUID.randomUUID().toString(),
                analysis = fallbackAnalysis,
                status = DiagnosticStatus.FALLBACK,
                alterationPlan = plan,
                technicalVerdict = fallbackAnalysis.probableCause,
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        }

        return try {
            val prompt = buildAnalysisPrompt(problemCategory, problemDescription, fabric)
            val jsonResponseText = GeminiService.analyzeGarment(prompt, base64Images)
            val analysis = parseGeminiResponse(
                jsonText = jsonResponseText,
                problemCategory = problemCategory,
                problemDescription = problemDescription,
                fabric = fabric,
                pricePaid = pricePaid,
                photoUris = photoUris
            )

            val primaryAlteration = analysis.alterationOptions.firstOrNull()
            val plan = primaryAlteration?.let {
                AlterationPlan(primaryAlteration = it, alternativeAlterations = analysis.alterationOptions.drop(1))
            }

            val status = when (analysis.confidence) {
                ConfidenceLevel.HIGH -> DiagnosticStatus.SUCCESS
                ConfidenceLevel.MEDIUM -> DiagnosticStatus.SUCCESS
                ConfidenceLevel.LOW -> DiagnosticStatus.LOW_CONFIDENCE
            }

            DiagnosticResult(
                id = UUID.randomUUID().toString(),
                analysis = analysis,
                status = status,
                alterationPlan = plan,
                technicalVerdict = "${analysis.garmentType} • ${analysis.possibleSolution}",
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            Log.w(TAG, "Gemini vision analysis failed or fallback triggered: ${e.message}")
            val fallbackAnalysis = DiagnosticEngine.generateDeterministicAnalysis(
                garmentName = "Peça de vestuário",
                problemDescription = problemDescription,
                problemCategory = problemCategory,
                fabric = fabric,
                pricePaid = pricePaid,
                photoUris = photoUris
            )
            val primaryAlteration = fallbackAnalysis.alterationOptions.firstOrNull()
            val plan = primaryAlteration?.let {
                AlterationPlan(primaryAlteration = it, alternativeAlterations = fallbackAnalysis.alterationOptions.drop(1))
            }
            DiagnosticResult(
                id = UUID.randomUUID().toString(),
                analysis = fallbackAnalysis,
                status = DiagnosticStatus.FALLBACK,
                alterationPlan = plan,
                technicalVerdict = fallbackAnalysis.probableCause,
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        }
    }

    private fun buildAnalysisPrompt(
        problemCategory: String,
        problemDescription: String,
        fabric: String?
    ): String {
        return """
Você é o consultor técnico de alta costura e alfaiataria do aplicativo Ajusta.
Analise a imagem da roupa fornecida pelo usuário.

Contexto fornecido pelo usuário:
- Categoria do problema relatado: "$problemCategory"
- Descrição do usuário: "$problemDescription"
- Tecido informado: "${fabric ?: "Não informado"}"

Diretrizes estritas:
1. NUNCA finja certeza absoluta.
2. Diferencie claramente:
   - "whatWeObserved": O que pode ser visto fisicamente na imagem.
   - "probableCause": Hipótese técnica baseada na imagem e no relato do usuário.
   - "possibleSolution": Alterações viáveis que podem ser consideradas.
   - "whatNeedsConfirmation": Aspectos que EXIGEM inspeção presencial (margem de costura, cós, zíper, folga, forro).
3. NUNCA invente preços (o aplicativo calcula os preços em motor separado).
4. NUNCA invente composição exata de tecido ou medidas em centímetros se não puder vê-las.
5. Classifique a complexidade (LOW, MEDIUM, HIGH, SPECIALIST) e risco (LOW, MEDIUM, HIGH).

Retorne EXCLUSIVAMENTE um objeto JSON válido no seguinte formato:
{
  "garmentType": "Ex: Calça jeans, Vestido, Camisa social, Blazer",
  "garmentTypeId": "jeans | pants | shirt | blouse | dress | skirt | blazer | jacket | coat | suit",
  "visibleCharacteristics": "Características visíveis da peça e caimento",
  "problemSummary": "Resumo objetivo do problema observado",
  "confidence": "HIGH | MEDIUM | LOW",
  "whatWeObserved": "Texto claro sobre o que é visto",
  "probableCause": "Hipótese técnica de modelagem ou tecido",
  "possibleSolution": "Solução recomendada para o ajuste",
  "whatNeedsConfirmation": "Itens para checagem presencial",
  "alterationOptions": [
    {
      "id": "waist_adjustment | hem_invisible | hem_original | rolled_hem | sleeve_adjustment_placket | shoulder_reconstruction | crotch_adjustment | invisible_zipper_replacement",
      "name": "Nome do ajuste",
      "complexity": "LOW | MEDIUM | HIGH | SPECIALIST",
      "description": "Explicação técnica sucinta",
      "requiresFitting": true,
      "notes": "Observações sobre a execução",
      "possibleRisks": ["Risco 1", "Risco 2"]
    }
  ],
  "riskLevel": "LOW | MEDIUM | HIGH",
  "riskSummary": "Avaliação geral do risco"
}
""".trimIndent()
    }

    private fun parseGeminiResponse(
        jsonText: String,
        problemCategory: String,
        problemDescription: String,
        fabric: String?,
        pricePaid: Double?,
        photoUris: List<String>
    ): GarmentAnalysis {
        val cleanJson = jsonText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(cleanJson)

        val garmentType = json.optString("garmentType", "Peça de vestuário")
        val garmentTypeId = json.optString("garmentTypeId", "pants")
        val visibleCharacteristics = json.optString("visibleCharacteristics", "Peça de vestuário em análise.")
        val problemSummary = json.optString("problemSummary", problemDescription.ifBlank { problemCategory })
        val confidenceStr = json.optString("confidence", "HIGH")
        val confidence = try { ConfidenceLevel.valueOf(confidenceStr) } catch (_: Exception) { ConfidenceLevel.MEDIUM }

        val whatWeObserved = json.optString("whatWeObserved", "Observada alteração no caimento da peça.")
        val probableCause = json.optString("probableCause", "Desproporção entre a modelagem da peça e as medidas do corpo.")
        val possibleSolution = json.optString("possibleSolution", "Ajuste técnico de costura.")
        val whatNeedsConfirmation = json.optString("whatNeedsConfirmation", "Verificação da margem interna e elasticidade.")

        val riskLevelStr = json.optString("riskLevel", "MEDIUM")
        val riskLevel = try { RiskLevel.valueOf(riskLevelStr) } catch (_: Exception) { RiskLevel.MEDIUM }
        val riskSummary = json.optString("riskSummary", "Alteração técnica a ser avaliada presencialmente.")

        // Parse alteration options
        val alterationOptions = mutableListOf<AlterationOption>()
        val optionsArray = json.optJSONArray("alterationOptions")
        if (optionsArray != null && optionsArray.length() > 0) {
            for (i in 0 until optionsArray.length()) {
                val opt = optionsArray.getJSONObject(i)
                val compStr = opt.optString("complexity", "MEDIUM")
                val complexity = try { ComplexityLevel.valueOf(compStr) } catch (_: Exception) { ComplexityLevel.MEDIUM }

                val risksList = mutableListOf<String>()
                val risksArray = opt.optJSONArray("possibleRisks")
                if (risksArray != null) {
                    for (r in 0 until risksArray.length()) {
                        risksList.add(risksArray.getString(r))
                    }
                }

                alterationOptions.add(
                    AlterationOption(
                        id = opt.optString("id", "waist_adjustment"),
                        name = opt.optString("name", "Ajuste recomendado"),
                        complexity = complexity,
                        description = opt.optString("description", ""),
                        requiresFitting = opt.optBoolean("requiresFitting", true),
                        notes = opt.optString("notes", ""),
                        possibleRisks = risksList
                    )
                )
            }
        }

        // If none returned by JSON, plan with AlterationPlanner
        if (alterationOptions.isEmpty()) {
            val plan = AlterationPlanner.planFor(garmentTypeId, problemCategory)
            alterationOptions.add(plan.primaryAlteration)
            alterationOptions.addAll(plan.alternativeAlterations)
        }

        val primaryAlteration = alterationOptions.first()

        // Calculate Price Estimate with PriceEstimateEngine (never LLM invented!)
        val priceEstimate = PriceEstimateEngine.calculateEstimate(
            PricingContext(
                garmentTypeId = garmentTypeId,
                alterationId = primaryAlteration.id,
                complexity = primaryAlteration.complexity,
                fabricId = fabric?.lowercase(),
                hasFineFinish = garmentTypeId in listOf("blazer", "suit", "dress")
            )
        )

        val isStructural = (riskLevel == RiskLevel.HIGH || primaryAlteration.id in listOf("shoulder_reconstruction", "lining_repair"))
        val recommendation = RecommendationEngine.generateRecommendation(
            pricePaid = pricePaid,
            priceEstimate = priceEstimate,
            riskLevel = riskLevel,
            isStructural = isStructural
        )

        val limitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução."

        val riskAssessment = RiskAssessment(
            level = riskLevel,
            summary = riskSummary,
            specificRisks = primaryAlteration.possibleRisks,
            requiresInPersonEvaluation = (riskLevel == RiskLevel.HIGH)
        )

        return GarmentAnalysis(
            id = UUID.randomUUID().toString(),
            garmentType = garmentType,
            visibleCharacteristics = visibleCharacteristics,
            problemSummary = problemSummary,
            problemCategory = problemCategory,
            confidence = confidence,
            whatWeObserved = whatWeObserved,
            probableCause = probableCause,
            possibleSolution = possibleSolution,
            whatNeedsConfirmation = whatNeedsConfirmation,
            beforeYouAlterLimitations = limitations,
            alterationOptions = alterationOptions,
            riskAssessment = riskAssessment,
            priceEstimate = priceEstimate,
            recommendation = recommendation,
            photoUris = photoUris,
            fabric = fabric,
            userPaidPrice = pricePaid
        )
    }
}
