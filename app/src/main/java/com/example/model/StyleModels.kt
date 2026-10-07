package com.example.model

data class CustomizationIdea(
    val title: String,
    val description: String,
    val difficulty: String
)

data class AccessoryItem(
    val category: String,
    val itemDescription: String,
    val tip: String
)

data class WardrobeLook(
    val occasion: String,
    val pairingDescription: String,
    val whyItWorks: String
)

data class StyleCustomizationSuggestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val styleConcept: String,
    val customizationIdeas: List<CustomizationIdea>,
    val accessoryCombinations: List<AccessoryItem>,
    val wardrobeLooks: List<WardrobeLook>,
    val colorPalette: List<String> = emptyList()
)
