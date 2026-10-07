package com.example.model

data class TutorialStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val tip: String? = null
)

data class RepairTutorial(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val category: String,
    val difficulty: String,
    val estimatedTimeMinutes: Int,
    val toolsNeeded: List<String>,
    val steps: List<TutorialStep>,
    val commonMistakes: List<String>,
    val tailorTip: String
)
