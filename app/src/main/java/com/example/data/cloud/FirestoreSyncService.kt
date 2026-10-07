package com.example.data.cloud

import android.util.Log
import com.example.model.ComplexityLevel
import com.example.model.ConfidenceLevel
import com.example.model.GarmentAnalysis
import com.example.model.PriceEstimate
import com.example.model.ProfessionalOrder
import com.example.model.RiskAssessment
import com.example.model.RiskLevel
import com.example.model.WorthItRecommendation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirestoreSyncService {
    private const val TAG = "FirestoreSyncService"

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun saveAnalysisToFirestore(userId: String, analysis: GarmentAnalysis): Result<Unit> {
        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("saved_analyses")
                .document(analysis.id)

            val data = hashMapOf(
                "id" to analysis.id,
                "garmentType" to analysis.garmentType,
                "visibleCharacteristics" to analysis.visibleCharacteristics,
                "problemSummary" to analysis.problemSummary,
                "problemCategory" to analysis.problemCategory,
                "confidence" to analysis.confidence.name,
                "whatWeObserved" to analysis.whatWeObserved,
                "probableCause" to analysis.probableCause,
                "possibleSolution" to analysis.possibleSolution,
                "whatNeedsConfirmation" to analysis.whatNeedsConfirmation,
                "beforeYouAlterLimitations" to analysis.beforeYouAlterLimitations,
                "priceMin" to analysis.priceEstimate.minPrice,
                "priceMax" to analysis.priceEstimate.maxPrice,
                "priceBase" to analysis.priceEstimate.basePrice,
                "priceFormatted" to analysis.priceEstimate.formattedRange,
                "riskLevel" to analysis.riskAssessment.level.name,
                "riskSummary" to analysis.riskAssessment.summary,
                "specificRisks" to analysis.riskAssessment.specificRisks,
                "requiresInPersonEvaluation" to analysis.riskAssessment.requiresInPersonEvaluation,
                "fabric" to (analysis.fabric ?: ""),
                "userPaidPrice" to (analysis.userPaidPrice ?: 0.0),
                "timestamp" to analysis.timestamp,
                "isTicketGenerated" to analysis.isTicketGenerated,
                "ticketNotes" to analysis.ticketNotes,
                "updatedAt" to System.currentTimeMillis()
            )

            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Analysis ${analysis.id} synced to Firestore for user $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving analysis to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun fetchAnalysesFromFirestore(userId: String): Result<List<GarmentAnalysis>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("saved_analyses")
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val garmentType = doc.getString("garmentType") ?: "Peça"
                val problemSummary = doc.getString("problemSummary") ?: ""
                val problemCategory = doc.getString("problemCategory") ?: "Ajuste geral"
                val confStr = doc.getString("confidence") ?: "MEDIUM"
                val confidence = try { ConfidenceLevel.valueOf(confStr) } catch (_: Exception) { ConfidenceLevel.MEDIUM }
                val whatWeObserved = doc.getString("whatWeObserved") ?: ""
                val probableCause = doc.getString("probableCause") ?: ""
                val possibleSolution = doc.getString("possibleSolution") ?: ""
                val priceMin = doc.getDouble("priceMin") ?: 40.0
                val priceMax = doc.getDouble("priceMax") ?: 80.0
                val riskLvlStr = doc.getString("riskLevel") ?: "MEDIUM"
                val riskLevel = try { RiskLevel.valueOf(riskLvlStr) } catch (_: Exception) { RiskLevel.MEDIUM }
                val riskSummary = doc.getString("riskSummary") ?: ""
                val fabric = doc.getString("fabric")
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val isTicket = doc.getBoolean("isTicketGenerated") ?: false
                val ticketNotes = doc.getString("ticketNotes") ?: ""

                GarmentAnalysis(
                    id = id,
                    garmentType = garmentType,
                    visibleCharacteristics = doc.getString("visibleCharacteristics") ?: "",
                    problemSummary = problemSummary,
                    problemCategory = problemCategory,
                    confidence = confidence,
                    whatWeObserved = whatWeObserved,
                    probableCause = probableCause,
                    possibleSolution = possibleSolution,
                    whatNeedsConfirmation = doc.getString("whatNeedsConfirmation") ?: "",
                    beforeYouAlterLimitations = doc.getString("beforeYouAlterLimitations") ?: "",
                    alterationOptions = emptyList(),
                    riskAssessment = RiskAssessment(
                        level = riskLevel,
                        summary = riskSummary,
                        specificRisks = emptyList(),
                        requiresInPersonEvaluation = doc.getBoolean("requiresInPersonEvaluation") ?: false
                    ),
                    priceEstimate = PriceEstimate(
                        minPrice = priceMin.toInt(),
                        maxPrice = priceMax.toInt(),
                        basePrice = (priceMin + priceMax) / 2.0
                    ),
                    recommendation = WorthItRecommendation(
                        garmentPricePaid = doc.getDouble("userPaidPrice"),
                        estimateRatioPercent = null,
                        pointsInFavor = listOf("Preservada no Firestore"),
                        pointsToConfirm = listOf("Verificar medidas com a costureira"),
                        summary = "Sincronizada via nuvem"
                    ),
                    photoUris = emptyList(),
                    fabric = fabric,
                    timestamp = timestamp,
                    isTicketGenerated = isTicket,
                    ticketNotes = ticketNotes
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun saveOrderToFirestore(userId: String, order: ProfessionalOrder): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("professional_orders")
                .document(order.id)
                .set(order, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving order to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }
}
