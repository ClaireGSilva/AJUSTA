package com.example.model

enum class ConfidenceLevel(val label: String, val badgeColor: Long) {
    HIGH("Confiança: Alta", 0xFF3D6B52),
    MEDIUM("Confiança: Média", 0xFF966E22),
    LOW("Confiança: Baixa", 0xFF9E3827)
}

enum class RiskLevel(val label: String, val description: String) {
    LOW(
        "Baixo Risco",
        "Pequenos reparos e alterações simples que preservam a estrutura original."
    ),
    MEDIUM(
        "Médio Risco",
        "Alterações que exigem conhecimento técnico e desmanche parcial de costuras."
    ),
    HIGH(
        "Alto Risco",
        "Recomendamos avaliação presencial de uma profissional antes de realizar qualquer alteração."
    )
}

enum class ComplexityLevel(val label: String, val iconColorHex: Long) {
    LOW("🟢 Baixa complexidade", 0xFF3D6B52),
    MEDIUM("🟡 Complexidade média", 0xFF966E22),
    HIGH("🔴 Alta complexidade", 0xFF9E3827),
    SPECIALIST("🟣 Especialista / Alfaiataria", 0xFF584175)
}

data class AlterationOption(
    val id: String,
    val name: String,
    val complexity: ComplexityLevel,
    val description: String,
    val requiresFitting: Boolean = true,
    val notes: String,
    val possibleRisks: List<String> = emptyList()
)

data class AlterationPlan(
    val primaryAlteration: AlterationOption,
    val alternativeAlterations: List<AlterationOption> = emptyList(),
    val stepByStepInspection: List<String> = emptyList(),
    val estimatedDurationHours: Double = 2.0
)

data class RiskAssessment(
    val level: RiskLevel,
    val summary: String,
    val specificRisks: List<String> = emptyList(),
    val requiresInPersonEvaluation: Boolean = (level == RiskLevel.HIGH)
)

data class PriceEstimate(
    val minPrice: Int,
    val maxPrice: Int,
    val basePrice: Double,
    val garmentFactor: Double = 1.0,
    val complexityFactor: Double = 1.0,
    val fabricFactor: Double = 1.0,
    val finishFactor: Double = 1.0,
    val urgencyFactor: Double = 1.0,
    val formattedRange: String = "R$ $minPrice–$maxPrice",
    val disclaimer: String = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
)

data class WorthItRecommendation(
    val garmentPricePaid: Double?,
    val estimateRatioPercent: Int?,
    val pointsInFavor: List<String>,
    val pointsToConfirm: List<String>,
    val summary: String
)

data class GarmentAnalysis(
    val id: String,
    val garmentType: String,
    val visibleCharacteristics: String,
    val problemSummary: String,
    val problemCategory: String,
    val confidence: ConfidenceLevel,
    val whatWeObserved: String,
    val probableCause: String,
    val possibleSolution: String,
    val whatNeedsConfirmation: String,
    val beforeYouAlterLimitations: String,
    val alterationOptions: List<AlterationOption>,
    val riskAssessment: RiskAssessment,
    val priceEstimate: PriceEstimate,
    val recommendation: WorthItRecommendation,
    val photoUris: List<String> = emptyList(),
    val fabric: String? = null,
    val userPaidPrice: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isTicketGenerated: Boolean = false,
    val ticketNotes: String = ""
)

enum class DiagnosticStatus {
    SUCCESS,
    LOW_CONFIDENCE,
    NEEDS_MORE_INFO,
    FALLBACK
}

data class DiagnosticResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val analysis: GarmentAnalysis,
    val status: DiagnosticStatus = DiagnosticStatus.SUCCESS,
    val alterationPlan: AlterationPlan? = null,
    val technicalVerdict: String = "",
    val executionTimeMs: Long = 0L
)

data class ServiceTicket(
    val id: String,
    val garmentName: String,
    val problem: String,
    val probableAlteration: String,
    val complexity: String,
    val fittingRequired: String,
    val pointsToCheck: List<String>,
    val priceEstimateFormatted: String,
    val generalNotes: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class OrderStatus(val label: String) {
    ORCAMENTO("Orçamento"),
    EM_ANDAMENTO("Em andamento"),
    PROVA_PENDENTE("Prova pendente"),
    CONCLUIDO("Concluído")
}

data class ProfessionalOrder(
    val id: String,
    val clientName: String,
    val clientPhone: String = "",
    val garmentType: String,
    val clientRequest: String,
    val preliminaryDiagnosis: String,
    val procedureSteps: List<String>,
    val materialsNeeded: String,
    val complexity: String,
    val attentionPoints: List<String>,
    val agreedPrice: Double? = null,
    val measurements: Map<String, String> = emptyMap(),
    val status: OrderStatus = OrderStatus.ORCAMENTO,
    val photoUris: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
