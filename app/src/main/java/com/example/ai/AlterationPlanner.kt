package com.example.ai

import com.example.knowledge.KnowledgeBase
import com.example.model.AlterationOption
import com.example.model.AlterationPlan
import com.example.model.ComplexityLevel

object AlterationPlanner {

    fun planFor(
        garmentTypeId: String,
        problemCategory: String,
        selectedAlterationId: String? = null
    ): AlterationPlan {
        val garment = KnowledgeBase.garments[garmentTypeId]
        val targetAlterationId = selectedAlterationId
            ?: deduceAlterationId(garmentTypeId, problemCategory)

        val targetDef = KnowledgeBase.alterations[targetAlterationId]
            ?: KnowledgeBase.alterations["waist_adjustment"]!!

        val primary = AlterationOption(
            id = targetDef.id,
            name = targetDef.namePt,
            complexity = targetDef.complexity,
            description = getAlterationDescription(targetDef.id),
            requiresFitting = targetDef.requiresFitting,
            notes = "Avaliação da construção interna e simetria das costuras antes de qualquer corte.",
            possibleRisks = targetDef.risks
        )

        // Suggest alternatives or complementary adjustments
        val alternatives = garment?.commonAlterations
            ?.filter { it != targetDef.id }
            ?.take(2)
            ?.mapNotNull { KnowledgeBase.alterations[it] }
            ?.map {
                AlterationOption(
                    id = it.id,
                    name = it.namePt,
                    complexity = it.complexity,
                    description = getAlterationDescription(it.id),
                    requiresFitting = it.requiresFitting,
                    notes = "Possível ajuste complementar para equilibrar a silhueta da peça.",
                    possibleRisks = it.risks
                )
            } ?: emptyList()

        val steps = listOf(
            "1. Avaliar construção da peça e tecido.",
            "2. Verificar margem interna de costura disponível.",
            "3. Verificar zíper, cós, forro e acabamento original.",
            "4. Marcar com alfinetes/giz no corpo do cliente.",
            "5. Fazer prova para validação do caimento.",
            "6. Executar o corte e costura técnica.",
            "7. Reavaliar simetria e acabamento a ferro.",
            "8. Finalizar pespontos e entrega."
        )

        return AlterationPlan(
            primaryAlteration = primary,
            alternativeAlterations = alternatives,
            stepByStepInspection = steps,
            estimatedDurationHours = if (targetDef.complexity == ComplexityLevel.HIGH) 3.5 else 1.5
        )
    }

    private fun deduceAlterationId(garmentTypeId: String, problemCategory: String): String {
        val lower = problemCategory.lowercase()
        return when {
            lower.contains("larga") || lower.contains("cintura") -> "waist_adjustment"
            lower.contains("apertada") -> "side_seam_adjustment"
            lower.contains("comprida") || lower.contains("barra") -> {
                if (garmentTypeId == "jeans") "hem_original" else "hem"
            }
            lower.contains("manga") -> "sleeve_adjustment"
            lower.contains("ombro") -> "shoulder_adjustment"
            lower.contains("zíper") || lower.contains("ziper") -> "zipper_replacement"
            lower.contains("rasgo") || lower.contains("reparar") -> "tear_repair"
            else -> "waist_adjustment"
        }
    }

    private fun getAlterationDescription(alterationId: String): String {
        return when (alterationId) {
            "waist_adjustment" -> "Pode ser realizado de diferentes formas dependendo da construção da peça: pences laterais, centro das costas ou rebaixamento de cós."
            "hem_original" -> "Preserva a borda desfiada ou puída original do jeans, recolocando-a com acabamento invisível ou pespontado."
            "hem" -> "Redução no comprimento com barra tradicional, italiana dobrada ou ponto invisível em peças de alfaiataria."
            "sleeve_adjustment" -> "Ajuste na altura ideal do punho (osso do pulso), preservando carcela e botões."
            "shoulder_adjustment" -> "Ajuste de alta precisão que redefine o encaixe da manga na cava. Exige reposicionamento de ombreiras."
            "side_seam_adjustment" -> "Redução ou ampliação pelas costuras laterais para modelar o tronco ou quadril."
            "zipper_replacement" -> "Substituição do fecho original danificado mantendo a técnica de inserção de fábrica."
            "tear_repair" -> "Cerzimento técnico combinando entretela de reforço com costura direcional na trama."
            "lining_repair" -> "Ajuste ou substituição de forro interno garantindo folga adequada para a movimentação."
            else -> "Ajuste técnico especializado para recuperação do caimento original."
        }
    }
}
