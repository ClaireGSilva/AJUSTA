package com.example.data.repository

import com.example.data.local.DiagnosticResultDao
import com.example.data.local.DiagnosticResultEntity
import com.example.data.local.GarmentAnalysisDao
import com.example.data.local.GarmentAnalysisEntity
import com.example.data.local.JsonConverters
import com.example.model.ConfidenceLevel
import com.example.model.DiagnosticResult
import com.example.model.DiagnosticStatus
import com.example.model.GarmentAnalysis
import com.example.model.PriceEstimate
import com.example.model.RiskAssessment
import com.example.model.RiskLevel
import com.example.model.WorthItRecommendation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GarmentRepository(
    private val dao: GarmentAnalysisDao,
    private val diagnosticDao: DiagnosticResultDao? = null
) {

    val allAnalyses: Flow<List<GarmentAnalysis>> = dao.getAllSavedAnalyses().map { list ->
        list.map { it.toDomain() }
    }

    val allDiagnosticResults: Flow<List<DiagnosticResult>>? = diagnosticDao?.getAllDiagnosticResults()?.map { list ->
        list.mapNotNull { entity ->
            val analysisEntity = dao.getAnalysisById(entity.analysisId)
            analysisEntity?.let {
                DiagnosticResult(
                    id = entity.id,
                    analysis = it.toDomain(),
                    status = try { DiagnosticStatus.valueOf(entity.status) } catch (_: Exception) { DiagnosticStatus.SUCCESS },
                    alterationPlan = JsonConverters.jsonToAlterationPlan(entity.alterationPlanJson),
                    technicalVerdict = entity.technicalVerdict,
                    executionTimeMs = entity.executionTimeMs
                )
            }
        }
    }

    suspend fun saveAnalysis(analysis: GarmentAnalysis, isTicket: Boolean = false, ticketNotes: String = "") {
        dao.insertAnalysis(analysis.toEntity(isTicket, ticketNotes))
    }

    suspend fun getAnalysisById(id: String): GarmentAnalysis? {
        return dao.getAnalysisById(id)?.toDomain()
    }

    suspend fun deleteAnalysis(id: String) {
        dao.deleteById(id)
        diagnosticDao?.deleteResultByAnalysisId(id)
    }

    suspend fun saveDiagnosticResult(
        result: DiagnosticResult,
        isTicket: Boolean = false,
        ticketNotes: String = ""
    ) {
        // Save the analysis first
        saveAnalysis(result.analysis, isTicket, ticketNotes)

        // Save the diagnostic result
        diagnosticDao?.insertDiagnosticResult(
            DiagnosticResultEntity(
                id = result.id,
                analysisId = result.analysis.id,
                status = result.status.name,
                technicalVerdict = result.technicalVerdict,
                executionTimeMs = result.executionTimeMs,
                alterationPlanJson = JsonConverters.alterationPlanToJson(result.alterationPlan),
                timestamp = result.analysis.timestamp
            )
        )
    }

    suspend fun getDiagnosticResultById(id: String): DiagnosticResult? {
        val entity = diagnosticDao?.getResultById(id) ?: return null
        val analysisEntity = dao.getAnalysisById(entity.analysisId) ?: return null
        return DiagnosticResult(
            id = entity.id,
            analysis = analysisEntity.toDomain(),
            status = try { DiagnosticStatus.valueOf(entity.status) } catch (_: Exception) { DiagnosticStatus.SUCCESS },
            alterationPlan = JsonConverters.jsonToAlterationPlan(entity.alterationPlanJson),
            technicalVerdict = entity.technicalVerdict,
            executionTimeMs = entity.executionTimeMs
        )
    }

    suspend fun deleteDiagnosticResult(id: String) {
        val entity = diagnosticDao?.getResultById(id)
        if (entity != null) {
            dao.deleteById(entity.analysisId)
            diagnosticDao.deleteResultById(id)
        }
    }

    private fun GarmentAnalysis.toEntity(isTicket: Boolean, ticketNotes: String): GarmentAnalysisEntity {
        return GarmentAnalysisEntity(
            id = id,
            garmentType = garmentType,
            visibleCharacteristics = visibleCharacteristics,
            problemSummary = problemSummary,
            problemCategory = problemCategory,
            confidence = confidence.name,
            whatWeObserved = whatWeObserved,
            probableCause = probableCause,
            possibleSolution = possibleSolution,
            whatNeedsConfirmation = whatNeedsConfirmation,
            beforeYouAlterLimitations = beforeYouAlterLimitations,
            alterationOptionsJson = JsonConverters.alterationOptionsToJson(alterationOptions),
            riskLevel = riskAssessment.level.name,
            riskSummary = riskAssessment.summary,
            specificRisksJson = JsonConverters.stringListToJson(riskAssessment.specificRisks),
            requiresInPersonEvaluation = riskAssessment.requiresInPersonEvaluation,
            priceMin = priceEstimate.minPrice,
            priceMax = priceEstimate.maxPrice,
            priceBase = priceEstimate.basePrice,
            priceGarmentFactor = priceEstimate.garmentFactor,
            priceComplexityFactor = priceEstimate.complexityFactor,
            priceFabricFactor = priceEstimate.fabricFactor,
            priceFinishFactor = priceEstimate.finishFactor,
            priceDisclaimer = priceEstimate.disclaimer,
            recommendationSummary = recommendation.summary,
            pointsInFavorJson = JsonConverters.stringListToJson(recommendation.pointsInFavor),
            pointsToConfirmJson = JsonConverters.stringListToJson(recommendation.pointsToConfirm),
            photoUrisSerialized = photoUris.joinToString(","),
            fabric = fabric,
            userPaidPrice = userPaidPrice,
            timestamp = timestamp,
            isTicketGenerated = isTicket,
            ticketNotes = ticketNotes
        )
    }

    private fun GarmentAnalysisEntity.toDomain(): GarmentAnalysis {
        val conf = try { ConfidenceLevel.valueOf(confidence) } catch (_: Exception) { ConfidenceLevel.MEDIUM }
        val riskLvl = try { RiskLevel.valueOf(riskLevel) } catch (_: Exception) { RiskLevel.MEDIUM }
        val photos = if (photoUrisSerialized.isNotBlank()) photoUrisSerialized.split(",").filter { it.isNotBlank() } else emptyList()

        val options = JsonConverters.jsonToAlterationOptions(alterationOptionsJson)
        val specificRisks = JsonConverters.jsonToStringList(specificRisksJson)
        val pointsInFavor = JsonConverters.jsonToStringList(pointsInFavorJson)
        val pointsToConfirm = JsonConverters.jsonToStringList(pointsToConfirmJson)

        val estimate = PriceEstimate(
            minPrice = priceMin,
            maxPrice = priceMax,
            basePrice = priceBase,
            garmentFactor = priceGarmentFactor,
            complexityFactor = priceComplexityFactor,
            fabricFactor = priceFabricFactor,
            finishFactor = priceFinishFactor,
            disclaimer = priceDisclaimer
        )

        val rec = WorthItRecommendation(
            garmentPricePaid = userPaidPrice,
            estimateRatioPercent = if (userPaidPrice != null && userPaidPrice > 0) {
                (((priceMin + priceMax) / 2.0) / userPaidPrice * 100).toInt()
            } else null,
            pointsInFavor = if (pointsInFavor.isNotEmpty()) pointsInFavor else listOf("Peça preservada no seu acervo pessoal."),
            pointsToConfirm = if (pointsToConfirm.isNotEmpty()) pointsToConfirm else listOf("Revise a ficha antes de enviar à costureira."),
            summary = recommendationSummary.ifBlank { "Histórico preservado localmente no dispositivo." }
        )

        return GarmentAnalysis(
            id = id,
            garmentType = garmentType,
            visibleCharacteristics = visibleCharacteristics.ifBlank { "Peça registrada no atelier." },
            problemSummary = problemSummary,
            problemCategory = problemCategory,
            confidence = conf,
            whatWeObserved = whatWeObserved,
            probableCause = probableCause,
            possibleSolution = possibleSolution,
            whatNeedsConfirmation = whatNeedsConfirmation,
            beforeYouAlterLimitations = beforeYouAlterLimitations,
            alterationOptions = options,
            riskAssessment = RiskAssessment(
                level = riskLvl,
                summary = riskSummary,
                specificRisks = specificRisks,
                requiresInPersonEvaluation = requiresInPersonEvaluation
            ),
            priceEstimate = estimate,
            recommendation = rec,
            photoUris = photos,
            fabric = fabric,
            userPaidPrice = userPaidPrice,
            timestamp = timestamp,
            isTicketGenerated = isTicketGenerated,
            ticketNotes = ticketNotes
        )
    }
}
