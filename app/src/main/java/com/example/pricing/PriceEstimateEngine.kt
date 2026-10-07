package com.example.pricing

import com.example.knowledge.KnowledgeBase
import com.example.model.ComplexityLevel
import com.example.model.PriceEstimate
import kotlin.math.roundToInt

data class PricingContext(
    val garmentTypeId: String,
    val alterationId: String,
    val complexity: ComplexityLevel = ComplexityLevel.MEDIUM,
    val fabricId: String? = null,
    val hasFineFinish: Boolean = false,
    val isUrgent: Boolean = false,
    val regionCity: String = "Média Brasil"
)

object PriceEstimateEngine {

    /**
     * Calculates the estimated price range using the exact formula:
     * basePrice * garmentFactor * complexityFactor * fabricFactor * finishFactor * urgencyFactor
     * and maps it to a realistic market range (min - max).
     */
    fun calculateEstimate(context: PricingContext): PriceEstimate {
        val alteration = KnowledgeBase.alterations[context.alterationId]
        val garment = KnowledgeBase.garments[context.garmentTypeId]
        val fabric = context.fabricId?.let { KnowledgeBase.fabrics[it] }

        // 1. Base price of the alteration
        val basePrice = alteration?.defaultBasePrice ?: 45.0

        // 2. Garment factor (e.g. blazer 1.7, jeans 1.15, shirt 1.1)
        val garmentFactor = garment?.baseLaborFactor ?: 1.0

        // 3. Complexity factor
        val complexityFactor = when (context.complexity) {
            ComplexityLevel.LOW -> 0.85
            ComplexityLevel.MEDIUM -> 1.0
            ComplexityLevel.HIGH -> 1.45
            ComplexityLevel.SPECIALIST -> 1.9
        }

        // 4. Fabric factor (delicate silks, leather, or thick fabrics require specialist handling)
        val fabricFactor = fabric?.difficultyFactor ?: 1.0

        // 5. Finish factor (e.g. hand stitching, invisible hem, silk piping)
        val finishFactor = if (context.hasFineFinish) 1.25 else 1.0

        // 6. Urgency factor
        val urgencyFactor = if (context.isUrgent) 1.3 else 1.0

        // Subtotal calculation
        val calculatedSubtotal = basePrice * garmentFactor * complexityFactor * fabricFactor * finishFactor * urgencyFactor

        // Generate conservative min and max range (usually -15% to +25% with rounding to multiples of 5)
        val minRaw = calculatedSubtotal * 0.85
        val maxRaw = calculatedSubtotal * 1.25

        val minPrice = roundToStep(minRaw, 5).coerceAtLeast(25)
        val maxPrice = roundToStep(maxRaw, 5).coerceAtLeast(minPrice + 15)

        return PriceEstimate(
            minPrice = minPrice,
            maxPrice = maxPrice,
            basePrice = basePrice,
            garmentFactor = garmentFactor,
            complexityFactor = complexityFactor,
            fabricFactor = fabricFactor,
            finishFactor = finishFactor,
            urgencyFactor = urgencyFactor,
            formattedRange = "R$ $minPrice–$maxPrice",
            disclaimer = "Estimativa inicial. O valor real depende da construção da peça, tecido, acabamento, região e profissional."
        )
    }

    private fun roundToStep(value: Double, step: Int): Int {
        val rounded = (value / step).roundToInt() * step
        return rounded
    }
}
