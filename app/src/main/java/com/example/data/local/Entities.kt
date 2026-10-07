package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "saved_analyses")
data class GarmentAnalysisEntity(
    @PrimaryKey val id: String,
    val garmentType: String,
    val visibleCharacteristics: String,
    val problemSummary: String,
    val problemCategory: String,
    val confidence: String,
    val whatWeObserved: String,
    val probableCause: String,
    val possibleSolution: String,
    val whatNeedsConfirmation: String,
    val beforeYouAlterLimitations: String,
    val alterationOptionsJson: String,
    val riskLevel: String,
    val riskSummary: String,
    val specificRisksJson: String,
    val requiresInPersonEvaluation: Boolean,
    val priceMin: Int,
    val priceMax: Int,
    val priceBase: Double,
    val priceGarmentFactor: Double,
    val priceComplexityFactor: Double,
    val priceFabricFactor: Double,
    val priceFinishFactor: Double,
    val priceDisclaimer: String,
    val recommendationSummary: String,
    val pointsInFavorJson: String,
    val pointsToConfirmJson: String,
    val photoUrisSerialized: String,
    val fabric: String?,
    val userPaidPrice: Double?,
    val timestamp: Long,
    val isTicketGenerated: Boolean = false,
    val ticketNotes: String = ""
)

@Entity(
    tableName = "diagnostic_results",
    indices = [Index(value = ["analysisId"])]
)
data class DiagnosticResultEntity(
    @PrimaryKey val id: String,
    val analysisId: String,
    val status: String,
    val technicalVerdict: String,
    val executionTimeMs: Long,
    val alterationPlanJson: String,
    val timestamp: Long
)

@Entity(tableName = "professional_orders")
data class ProfessionalOrderEntity(
    @PrimaryKey val id: String,
    val clientName: String,
    val clientPhone: String,
    val garmentType: String,
    val clientRequest: String,
    val preliminaryDiagnosis: String,
    val procedureStepsJson: String,
    val materialsNeeded: String,
    val complexity: String,
    val attentionPointsJson: String,
    val agreedPrice: Double?,
    val measurementsJson: String,
    val status: String,
    val photoUrisSerialized: String,
    val createdAt: Long
)
