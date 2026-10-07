package com.example.util

import android.content.Context
import android.content.Intent
import com.example.model.GarmentAnalysis

object DiagnosisShareHelper {

    fun buildShareableDiagnosisText(analysis: GarmentAnalysis): String {
        return buildString {
            appendLine("✂️ DIAGNÓSTICO DE AJUSTE • AJUSTA")
            appendLine("───────────────────────────")
            appendLine("👗 Peça: ${analysis.garmentType}")
            if (!analysis.fabric.isNullOrBlank()) {
                appendLine("🧵 Tecido: ${analysis.fabric}")
            }
            appendLine("🔍 Problema relatado: ${analysis.problemSummary}")
            appendLine()
            appendLine("💡 Diagnóstico do Caimento:")
            appendLine(analysis.probableCause.ifBlank { analysis.whatWeObserved })
            appendLine()
            appendLine("📐 Ajuste Recomendado:")
            appendLine("• ${analysis.possibleSolution}")
            if (analysis.alterationOptions.isNotEmpty()) {
                analysis.alterationOptions.forEach { opt ->
                    appendLine("  - ${opt.name} (${opt.complexity.label})")
                }
            }
            appendLine()
            appendLine("💰 Estimativa de Custo:")
            appendLine("${analysis.priceEstimate.formattedRange} (independente de ateliê)")
            appendLine()
            appendLine("⚠️ Avaliação de Risco: ${analysis.riskAssessment.level.label}")
            appendLine(analysis.riskAssessment.summary)
            if (analysis.riskAssessment.specificRisks.isNotEmpty()) {
                appendLine("Pontos de atenção:")
                analysis.riskAssessment.specificRisks.forEach { risk ->
                    appendLine("• $risk")
                }
            }
            appendLine()
            appendLine("✨ Conclusão:")
            appendLine(analysis.recommendation.summary)
            appendLine()
            append("📲 Gerado no Ajusta • Seu Atelier Digital")
        }
    }

    fun shareDiagnosis(context: Context, analysis: GarmentAnalysis) {
        val shareText = buildShareableDiagnosisText(analysis)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Diagnóstico de Ajuste - ${analysis.garmentType}")
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(sendIntent, "Compartilhar Diagnóstico de Ajuste").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}
