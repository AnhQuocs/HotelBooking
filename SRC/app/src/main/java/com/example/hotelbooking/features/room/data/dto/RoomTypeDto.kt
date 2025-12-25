package com.example.hotelbooking.features.room.data.dto

data class RoomTypeDto(
    val hotelId: String? = null,
    val totalStock: Int? = null,
    val pricePerNight: Int? = null,
    val capacity: Int? = null,
    val roomSize: Int? = null,
    val imageUrl: String? = null,
    val name: Map<String, String>? = null,
    val description: Map<String, String>? = null,
    val bedType: Map<String, String>? = null,
    val bathroomType: Map<String, String>? = null,
    val amenities: List<AmenityDto> = emptyList(),
    val smokingPolicy: Boolean? = false,
    val petPolicy: Boolean? = true,
)

data class AmenityDto(
    val name: Map<String, String>? = null,
    val iconUrl: String? = null
)