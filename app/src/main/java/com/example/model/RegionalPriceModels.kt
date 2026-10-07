package com.example.model

data class RegionalPriceEstimate(
    val id: String = java.util.UUID.randomUUID().toString(),
    val region: String,
    val currencyCode: String = "BRL",
    val currencySymbol: String = "R$",
    val minPrice: Double,
    val maxPrice: Double,
    val averagePrice: Double,
    val formattedRange: String,
    val regionalMarketContext: String,
    val factorsImpactingPrice: List<String> = emptyList(),
    val averageTurnaroundDays: String = "2 a 5 dias úteis",
    val confidenceNote: String = "Estimativa calculada por IA com base nos custos médios da região."
)
