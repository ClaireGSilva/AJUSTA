package com.example.data.repository

import com.example.data.local.ProfessionalOrderDao
import com.example.data.local.ProfessionalOrderEntity
import com.example.model.OrderStatus
import com.example.model.ProfessionalOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ProfessionalRepository(private val dao: ProfessionalOrderDao) {

    val allOrders: Flow<List<ProfessionalOrder>> = dao.getAllOrders().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun saveOrder(order: ProfessionalOrder) {
        dao.insertOrder(order.toEntity())
    }

    suspend fun updateOrder(order: ProfessionalOrder) {
        dao.updateOrder(order.toEntity())
    }

    suspend fun deleteOrder(id: String) {
        dao.deleteOrderById(id)
    }

    private fun ProfessionalOrder.toEntity(): ProfessionalOrderEntity {
        val stepsJson = JSONArray(procedureSteps).toString()
        val attentionJson = JSONArray(attentionPoints).toString()
        val measJson = JSONObject(measurements).toString()

        return ProfessionalOrderEntity(
            id = id,
            clientName = clientName,
            clientPhone = clientPhone,
            garmentType = garmentType,
            clientRequest = clientRequest,
            preliminaryDiagnosis = preliminaryDiagnosis,
            procedureStepsJson = stepsJson,
            materialsNeeded = materialsNeeded,
            complexity = complexity,
            attentionPointsJson = attentionJson,
            agreedPrice = agreedPrice,
            measurementsJson = measJson,
            status = status.name,
            photoUrisSerialized = photoUris.joinToString(","),
            createdAt = createdAt
        )
    }

    private fun ProfessionalOrderEntity.toDomain(): ProfessionalOrder {
        val statusEnum = try { OrderStatus.valueOf(status) } catch (_: Exception) { OrderStatus.ORCAMENTO }

        val steps = mutableListOf<String>()
        try {
            val arr = JSONArray(procedureStepsJson)
            for (i in 0 until arr.length()) steps.add(arr.getString(i))
        } catch (_: Exception) {}

        val attention = mutableListOf<String>()
        try {
            val arr = JSONArray(attentionPointsJson)
            for (i in 0 until arr.length()) attention.add(arr.getString(i))
        } catch (_: Exception) {}

        val meas = mutableMapOf<String, String>()
        try {
            val obj = JSONObject(measurementsJson)
            for (k in obj.keys()) meas[k] = obj.getString(k)
        } catch (_: Exception) {}

        val photos = if (photoUrisSerialized.isNotBlank()) photoUrisSerialized.split(",") else emptyList()

        return ProfessionalOrder(
            id = id,
            clientName = clientName,
            clientPhone = clientPhone,
            garmentType = garmentType,
            clientRequest = clientRequest,
            preliminaryDiagnosis = preliminaryDiagnosis,
            procedureSteps = steps,
            materialsNeeded = materialsNeeded,
            complexity = complexity,
            attentionPoints = attention,
            agreedPrice = agreedPrice,
            measurements = meas,
            status = statusEnum,
            photoUris = photos,
            createdAt = createdAt
        )
    }
}
