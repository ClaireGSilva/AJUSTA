package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GarmentRepairStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GarmentProgressManager {
    private const val PREFS_NAME = "garment_repair_progress_prefs"
    private var prefs: SharedPreferences? = null

    private val _progressMapState = MutableStateFlow<Map<String, GarmentRepairStage>>(emptyMap())
    val progressMapState: StateFlow<Map<String, GarmentRepairStage>> = _progressMapState.asStateFlow()

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadAll()
        }
    }

    private fun loadAll() {
        val all = prefs?.all ?: return
        val map = mutableMapOf<String, GarmentRepairStage>()
        for ((key, value) in all) {
            if (value is String) {
                val stage = GarmentRepairStage.values().find { it.id == value }
                if (stage != null) {
                    map[key] = stage
                }
            }
        }
        _progressMapState.value = map
    }

    fun getStage(garmentId: String, isTicketGenerated: Boolean = false): GarmentRepairStage {
        val saved = _progressMapState.value[garmentId]
        if (saved != null) return saved

        return when {
            garmentId.contains("jeans", ignoreCase = true) -> GarmentRepairStage.CONCLUIDO
            garmentId.contains("vestido", ignoreCase = true) -> GarmentRepairStage.EM_ANDAMENTO
            isTicketGenerated -> GarmentRepairStage.FICHA_EMITIDA
            else -> GarmentRepairStage.ORCADO
        }
    }

    fun setStage(garmentId: String, stage: GarmentRepairStage) {
        val updated = _progressMapState.value.toMutableMap()
        updated[garmentId] = stage
        _progressMapState.value = updated
        prefs?.edit()?.putString(garmentId, stage.id)?.apply()
    }
}
