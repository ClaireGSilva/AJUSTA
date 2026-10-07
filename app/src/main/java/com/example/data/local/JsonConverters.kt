package com.example.data.local

import com.example.model.AlterationOption
import com.example.model.AlterationPlan
import com.example.model.ComplexityLevel
import org.json.JSONArray
import org.json.JSONObject

object JsonConverters {

    fun stringListToJson(list: List<String>): String {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    fun jsonToStringList(jsonStr: String?): List<String> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun alterationOptionsToJson(options: List<AlterationOption>): String {
        val jsonArray = JSONArray()
        for (opt in options) {
            val obj = JSONObject().apply {
                put("id", opt.id)
                put("name", opt.name)
                put("complexity", opt.complexity.name)
                put("description", opt.description)
                put("requiresFitting", opt.requiresFitting)
                put("notes", opt.notes)
                val risksArray = JSONArray()
                opt.possibleRisks.forEach { risksArray.put(it) }
                put("possibleRisks", risksArray)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    fun jsonToAlterationOptions(jsonStr: String?): List<AlterationOption> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<AlterationOption>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val complexityStr = obj.optString("complexity", "MEDIUM")
                val complexity = try {
                    ComplexityLevel.valueOf(complexityStr)
                } catch (_: Exception) {
                    ComplexityLevel.MEDIUM
                }

                val risksList = mutableListOf<String>()
                val risksArray = obj.optJSONArray("possibleRisks")
                if (risksArray != null) {
                    for (r in 0 until risksArray.length()) {
                        risksList.add(risksArray.getString(r))
                    }
                }

                list.add(
                    AlterationOption(
                        id = obj.optString("id", "waist_adjustment"),
                        name = obj.optString("name", "Ajuste"),
                        complexity = complexity,
                        description = obj.optString("description", ""),
                        requiresFitting = obj.optBoolean("requiresFitting", true),
                        notes = obj.optString("notes", ""),
                        possibleRisks = risksList
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun alterationPlanToJson(plan: AlterationPlan?): String {
        if (plan == null) return ""
        val obj = JSONObject().apply {
            put("primary", JSONObject().apply {
                put("id", plan.primaryAlteration.id)
                put("name", plan.primaryAlteration.name)
                put("complexity", plan.primaryAlteration.complexity.name)
                put("description", plan.primaryAlteration.description)
                put("requiresFitting", plan.primaryAlteration.requiresFitting)
                put("notes", plan.primaryAlteration.notes)
                val risksArray = JSONArray()
                plan.primaryAlteration.possibleRisks.forEach { risksArray.put(it) }
                put("possibleRisks", risksArray)
            })
            put("alternatives", alterationOptionsToJson(plan.alternativeAlterations))
            put("stepByStepInspection", stringListToJson(plan.stepByStepInspection))
            put("estimatedDurationHours", plan.estimatedDurationHours)
        }
        return obj.toString()
    }

    fun jsonToAlterationPlan(jsonStr: String?): AlterationPlan? {
        if (jsonStr.isNullOrBlank()) return null
        return try {
            val obj = JSONObject(jsonStr)
            val primaryObj = obj.getJSONObject("primary")
            val complexityStr = primaryObj.optString("complexity", "MEDIUM")
            val complexity = try {
                ComplexityLevel.valueOf(complexityStr)
            } catch (_: Exception) {
                ComplexityLevel.MEDIUM
            }

            val risksList = mutableListOf<String>()
            val risksArray = primaryObj.optJSONArray("possibleRisks")
            if (risksArray != null) {
                for (r in 0 until risksArray.length()) {
                    risksList.add(risksArray.getString(r))
                }
            }

            val primary = AlterationOption(
                id = primaryObj.optString("id", "primary"),
                name = primaryObj.optString("name", "Ajuste Primário"),
                complexity = complexity,
                description = primaryObj.optString("description", ""),
                requiresFitting = primaryObj.optBoolean("requiresFitting", true),
                notes = primaryObj.optString("notes", ""),
                possibleRisks = risksList
            )

            val alternatives = jsonToAlterationOptions(obj.optString("alternatives", ""))
            val inspection = jsonToStringList(obj.optString("stepByStepInspection", ""))
            val duration = obj.optDouble("estimatedDurationHours", 2.0)

            AlterationPlan(
                primaryAlteration = primary,
                alternativeAlterations = alternatives,
                stepByStepInspection = inspection,
                estimatedDurationHours = duration
            )
        } catch (_: Exception) {
            null
        }
    }
}
