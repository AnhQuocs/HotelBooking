package com.example.hotelbooking.features.hotel.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val adminIds: List<String>,
    val address: String,
    val shortAddress: String,
    val city: String,
    val country: String,
    val description: String,
    val thumbnailUrl: String,
    val amenities: List<String>,
    val pricePerNightMin: Int,
    val averageRating: Double,
    val numberOfReviews: Int,
    val latitude: Double,
    val longitude: Double,
    val checkInTime: String,
    val checkOutTime: String,
)
