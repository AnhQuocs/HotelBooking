package com.example.hotelbooking.features.hotel.domain.model

data class AdminHotel(
    val id: String,
    val rawName: Map<String, String>,
    val rawDescription: Map<String, String>,
    val rawAmenities: Map<String, List<String>>,

    val adminIds: List<String>,
    val rawAddress: Map<String, String>,
    val rawShortAddress: Map<String, String>,
    val rawCity: Map<String, String>,
    val rawCountry: Map<String, String>,
    val thumbnailUrl: String,
    val pricePerNightMin: Int,
    val latitude: Double,
    val longitude: Double,
    val checkInTime: String,
    val checkOutTime: String
)