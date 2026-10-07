package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GarmentAnalysisDao {
    @Query("SELECT * FROM saved_analyses ORDER BY timestamp DESC")
    fun getAllSavedAnalyses(): Flow<List<GarmentAnalysisEntity>>

    @Query("SELECT * FROM saved_analyses WHERE id = :id")
    suspend fun getAnalysisById(id: String): GarmentAnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(entity: GarmentAnalysisEntity)

    @Delete
    suspend fun deleteAnalysis(entity: GarmentAnalysisEntity)

    @Query("DELETE FROM saved_analyses WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface DiagnosticResultDao {
    @Query("SELECT * FROM diagnostic_results ORDER BY timestamp DESC")
    fun getAllDiagnosticResults(): Flow<List<DiagnosticResultEntity>>

    @Query("SELECT * FROM diagnostic_results WHERE id = :id")
    suspend fun getResultById(id: String): DiagnosticResultEntity?

    @Query("SELECT * FROM diagnostic_results WHERE analysisId = :analysisId LIMIT 1")
    suspend fun getResultByAnalysisId(analysisId: String): DiagnosticResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosticResult(entity: DiagnosticResultEntity)

    @Delete
    suspend fun deleteDiagnosticResult(entity: DiagnosticResultEntity)

    @Query("DELETE FROM diagnostic_results WHERE id = :id")
    suspend fun deleteResultById(id: String)

    @Query("DELETE FROM diagnostic_results WHERE analysisId = :analysisId")
    suspend fun deleteResultByAnalysisId(analysisId: String)
}

@Dao
interface ProfessionalOrderDao {
    @Query("SELECT * FROM professional_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<ProfessionalOrderEntity>>

    @Query("SELECT * FROM professional_orders WHERE id = :id")
    suspend fun getOrderById(id: String): ProfessionalOrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(entity: ProfessionalOrderEntity)

    @Update
    suspend fun updateOrder(entity: ProfessionalOrderEntity)

    @Delete
    suspend fun deleteOrder(entity: ProfessionalOrderEntity)

    @Query("DELETE FROM professional_orders WHERE id = :id")
    suspend fun deleteOrderById(id: String)
}
