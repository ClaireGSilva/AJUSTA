package com.example.model

data class TailorSpecialty(
    val id: String,
    val name: String,
    val iconEmoji: String
)

data class TailorServiceOffer(
    val serviceName: String,
    val priceEstimate: String,
    val turnaroundDays: String
)

data class TailorReview(
    val author: String,
    val rating: Float,
    val date: String,
    val comment: String,
    val garmentAdjusted: String
)

data class TailorProfile(
    val id: String,
    val name: String,
    val atelierName: String,
    val subtitle: String,
    val address: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val distanceKm: Double,
    val rating: Float,
    val reviewCount: Int,
    val specialties: List<String>,
    val phone: String,
    val whatsapp: String,
    val openingHours: String,
    val experienceYears: Int,
    val isVerified: Boolean = true,
    val bio: String,
    val services: List<TailorServiceOffer> = emptyList(),
    val reviews: List<TailorReview> = emptyList(),
    val acceptsEmergencyOrders: Boolean = true,
    val latitude: Double,
    val longitude: Double
)
