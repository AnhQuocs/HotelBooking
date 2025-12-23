package com.example.hotelbooking.features.hotel.data.dto

data class HotelDto(
    val name: Map<String, String> = emptyMap(),
    val description: Map<String, String> = emptyMap(),
    val amenities: Map<String, List<String>> = emptyMap(),

    val adminIds: List<String> = emptyList(),
    val address: Map<String, String> = emptyMap(),
    val shortAddress: Map<String, String> = emptyMap(),
    val city: Map<String, String> = emptyMap(),
    val country: Map<String, String> = emptyMap(),
    val thumbnailUrl: String = "",
    val pricePerNightMin: Int = 0,
    val averageRating: Double = 0.0,
    val numberOfReviews: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val checkInTime: String = "",
    val checkOutTime: String = ""
)