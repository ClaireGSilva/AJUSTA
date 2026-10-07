package com.example.ai

import com.example.model.PriceEstimate
import com.example.model.RiskLevel
import com.example.model.WorthItRecommendation
import kotlin.math.roundToInt

object RecommendationEngine {

    fun generateRecommendation(
        pricePaid: Double?,
        priceEstimate: PriceEstimate,
        riskLevel: RiskLevel,
        isStructural: Boolean
    ): WorthItRecommendation {
        val avgEstimate = (priceEstimate.minPrice + priceEstimate.maxPrice) / 2.0
        val ratioPercent = if (pricePaid != null && pricePaid > 0) {
            ((avgEstimate / pricePaid) * 100).roundToInt()
        } else {
            null
        }

        val pointsInFavor = mutableListOf<String>()
        val pointsToConfirm = mutableListOf<String>()

        // Points in favor
        pointsInFavor.add("Você realmente gosta do caimento geral, cor ou estampa da peça.")
        pointsInFavor.add("O tecido tem boa durabilidade e vale o investimento para prolongar o uso.")
        pointsInFavor.add("O ajuste preserva a estrutura original e valoriza sua silhueta.")
        pointsInFavor.add("Você pretende utilizá-la com frequência em seu dia a dia ou ocasiões especiais.")

        // Points to confirm before deciding
        if (isStructural || riskLevel == RiskLevel.HIGH) {
            pointsToConfirm.add("A alteração é estrutural: exige desmanche de costuras principais e prova presencial.")
        }
        if (ratioPercent != null && ratioPercent >= 60) {
            pointsToConfirm.add("O custo do ajuste estimado representa aproximadamente $ratioPercent% do valor pago pela peça.")
        } else {
            pointsToConfirm.add("Verifique se existe margem interna de tecido suficiente para a alteração.")
        }
        pointsToConfirm.add("Confirme o acabamento interno com a profissional antes de autorizar o corte.")

        val summary = if (ratioPercent != null) {
            "O ajuste representa cerca de $ratioPercent% do valor da peça. Avalie o valor afetivo, a qualidade do tecido e a frequência de uso antes de decidir."
        } else {
            "Ajustar uma peça bem escolhida é uma atitude consciente e elegante de moda sustentável."
        }

        return WorthItRecommendation(
            garmentPricePaid = pricePaid,
            estimateRatioPercent = ratioPercent,
            pointsInFavor = pointsInFavor,
            pointsToConfirm = pointsToConfirm,
            summary = summary
        )
    }
}
