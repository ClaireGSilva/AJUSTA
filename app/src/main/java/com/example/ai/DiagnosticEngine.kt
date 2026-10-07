package com.example.ai

import com.example.knowledge.KnowledgeBase
import com.example.model.AlterationOption
import com.example.model.ConfidenceLevel
import com.example.model.GarmentAnalysis
import com.example.model.RiskAssessment
import com.example.model.RiskLevel
import com.example.pricing.PriceEstimateEngine
import com.example.pricing.PricingContext
import java.util.UUID

object DiagnosticEngine {

    fun generateDeterministicAnalysis(
        garmentName: String,
        problemDescription: String,
        problemCategory: String,
        fabric: String?,
        pricePaid: Double?,
        photoUris: List<String>
    ): GarmentAnalysis {
        val garmentTypeId = mapGarmentToId(garmentName, problemDescription, fabric)
        val alterationPlan = AlterationPlanner.planFor(garmentTypeId, problemCategory)
        val primaryAlteration = alterationPlan.primaryAlteration

        val fabricId = mapFabricToId(fabric)
        val fabricDef = fabricId?.let { KnowledgeBase.fabrics[it] }

        // Determine Risk Level
        val riskLevel = when {
            garmentTypeId in listOf("blazer", "suit") -> RiskLevel.HIGH
            fabricDef?.riskLevel == RiskLevel.HIGH -> RiskLevel.HIGH
            primaryAlteration.complexity.name in listOf("HIGH", "SPECIALIST") -> RiskLevel.HIGH
            primaryAlteration.complexity.name == "MEDIUM" -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val riskSummary = when (riskLevel) {
            RiskLevel.LOW -> "Baixo risco estrutural. A intervenção é pontual e preserva a integridade original da peça."
            RiskLevel.MEDIUM -> "Risco moderado. Envolve desmanche parcial de costuras existentes e demanda precisão nas pences ou pespontos."
            RiskLevel.HIGH -> "Recomendamos avaliação presencial de uma profissional antes de realizar qualquer alteração estrutural nesta peça."
        }

        val specificRisks = mutableListOf<String>()
        if (riskLevel == RiskLevel.HIGH) {
            specificRisks.add("Risco de distorção da linha de ombro e lapela se houver desmonte da cava.")
            specificRisks.add("Tecidos estruturados exigem entretela e prensagem a vapor adequada.")
        } else if (riskLevel == RiskLevel.MEDIUM) {
            specificRisks.add("Pode haver marcação residual no local das costuras anteriores se o tecido estiver desbotado.")
        }

        val riskAssessment = RiskAssessment(
            level = riskLevel,
            summary = riskSummary,
            specificRisks = specificRisks,
            requiresInPersonEvaluation = (riskLevel == RiskLevel.HIGH)
        )

        // Calculate Price Estimate via PriceEstimateEngine
        val priceEstimate = PriceEstimateEngine.calculateEstimate(
            PricingContext(
                garmentTypeId = garmentTypeId,
                alterationId = primaryAlteration.id,
                complexity = primaryAlteration.complexity,
                fabricId = fabricId,
                hasFineFinish = (garmentTypeId in listOf("blazer", "suit", "dress"))
            )
        )

        // Recommendation
        val isStructural = (riskLevel == RiskLevel.HIGH || primaryAlteration.id in listOf("shoulder_adjustment", "lining_repair"))
        val recommendation = RecommendationEngine.generateRecommendation(
            pricePaid = pricePaid,
            priceEstimate = priceEstimate,
            riskLevel = riskLevel,
            isStructural = isStructural
        )

        val whatWeObserved = buildObservedText(garmentTypeId, problemCategory, problemDescription)
        val probableCause = buildProbableCauseText(garmentTypeId, problemCategory)
        val possibleSolution = buildPossibleSolutionText(garmentTypeId, primaryAlteration)
        val whatNeedsConfirmation = buildNeedsConfirmationText(garmentTypeId)
        val limitations = "A construção interna da peça não pode ser confirmada apenas pela fotografia. Uma profissional deverá verificar margens de costura, cós, zíperes e acabamento antes da execução."

        val optionsList = mutableListOf<AlterationOption>()
        optionsList.add(primaryAlteration)
        optionsList.addAll(alterationPlan.alternativeAlterations)

        return GarmentAnalysis(
            id = UUID.randomUUID().toString(),
            garmentType = KnowledgeBase.garments[garmentTypeId]?.namePt ?: garmentName,
            visibleCharacteristics = "Peça em ${fabricDef?.namePt ?: "tecido plano/estruturado"} com acabamento padrão.",
            problemSummary = if (problemDescription.isNotBlank()) problemDescription else problemCategory,
            problemCategory = problemCategory,
            confidence = ConfidenceLevel.HIGH,
            whatWeObserved = whatWeObserved,
            probableCause = probableCause,
            possibleSolution = possibleSolution,
            whatNeedsConfirmation = whatNeedsConfirmation,
            beforeYouAlterLimitations = limitations,
            alterationOptions = optionsList,
            riskAssessment = riskAssessment,
            priceEstimate = priceEstimate,
            recommendation = recommendation,
            photoUris = photoUris,
            fabric = fabricDef?.namePt ?: fabric,
            userPaidPrice = pricePaid
        )
    }

    private fun mapGarmentToId(name: String, desc: String, fabric: String?): String {
        val text = "$name $desc ${fabric ?: ""}".lowercase()
        return when {
            text.contains("jean") || text.contains("denim") -> "jeans"
            text.contains("blazer") || text.contains("paletó") -> "blazer"
            text.contains("camisa") -> "shirt"
            text.contains("blusa") -> "blouse"
            text.contains("vestido") -> "dress"
            text.contains("saia") -> "skirt"
            text.contains("terno") || text.contains("costume") -> "suit"
            text.contains("jaqueta") -> "jacket"
            text.contains("casaco") || text.contains("sobretudo") -> "coat"
            text.contains("calça") -> "pants"
            else -> "pants"
        }
    }

    private fun mapFabricToId(fabric: String?): String? {
        val f = fabric?.lowercase() ?: return null
        return when {
            f.contains("algod") -> "cotton"
            f.contains("jean") || f.contains("denim") -> "denim"
            f.contains("linho") -> "linen"
            f.contains("viscose") -> "viscose"
            f.contains("seda") -> "silk"
            f.contains("lã") || f.contains("la") -> "wool"
            f.contains("malha") -> "knit"
            f.contains("elast") || f.contains("lycra") -> "elastane"
            f.contains("couro") -> "leather"
            else -> null
        }
    }

    private fun buildObservedText(garmentTypeId: String, problem: String, desc: String): String {
        val lower = "$problem $desc".lowercase()
        return when {
            lower.contains("larga") || lower.contains("cintura") ->
                "Sobra perceptível de tecido na circunferência do cós/cintura, gerando afastamento da peça em relação à linha anatômica do corpo."
            lower.contains("apertada") ->
                "Tensão nas costuras laterais com repuxamento e rugas horizontais de esforço."
            lower.contains("comprida") || lower.contains("barra") ->
                "Excesso de comprimento acumulando dobras sobre o sapato ou arrastando na bainha."
            lower.contains("manga") ->
                "Comprimento da manga cobrindo parte da mão ou ultrapassando a articulação do punho."
            lower.contains("ombro") ->
                "A costura da cava ultrapassa a ponta do acrômio, criando caída desestruturada ou repuxo na axila."
            lower.contains("zíper") || lower.contains("ziper") ->
                "Trilho de zíper desalinhado ou cursor desgastado que não engata os dentes."
            else -> "Irregularidade no caimento e proporção visual em relação à estrutura da peça."
        }
    }

    private fun buildProbableCauseText(garmentTypeId: String, problem: String): String {
        val lower = problem.lowercase()
        return when {
            lower.contains("larga") ->
                "Divergência de medidas na modelagem padrão entre quadril e cintura, ou relaxamento natural da fibra do tecido com o uso contínuo."
            lower.contains("apertada") ->
                "Peça com pouca folga de vestibilidade (ease) para os movimentos normais do corpo."
            lower.contains("comprida") ->
                "Modelagem produzida com comprimento genérico para atender estaturas variadas com calçado alto."
            lower.contains("manga") ->
                "Proporção de braço da confecção superior à medida anatômica individual."
            lower.contains("ombro") ->
                "Largura de ombro a ombro incompatível com a estrutura óssea do usuário."
            lower.contains("zíper") ->
                "Fadiga de tração contínua na trava do cursor ou dente quebrado por atrito."
            else -> "Assimetria entre a modelagem industrializada da peça e as proporções individuais do corpo."
        }
    }

    private fun buildPossibleSolutionText(garmentTypeId: String, alteration: AlterationOption): String {
        return "Realizar ${alteration.name.lowercase()}. ${alteration.description}"
    }

    private fun buildNeedsConfirmationText(garmentTypeId: String): String {
        return when (garmentTypeId) {
            "jeans" -> "Espessura das costuras no cós traseiro, posição do passador de cinto central e sobra interna."
            "blazer", "suit" -> "Presença de ombreiras coladas ou soltas, entretela termocolante e folga do forro."
            "dress" -> "Comprimento do zíper invisível e se há corte ou fenda na lateral que impeça a redução."
            else -> "Margem de costura interna original e existência de marcas de lavagem/costura prévia."
        }
    }
}
