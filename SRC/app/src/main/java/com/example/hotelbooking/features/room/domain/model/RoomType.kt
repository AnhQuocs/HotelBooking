package com.example.hotelbooking.features.room.domain.model

data class RoomType(
    val id: String,
    val hotelId: String,
    val totalStock: Int,
    val pricePerNight: Int,
    val capacity: Int,
    val roomSize: Int,
    val imageUrl: String,
    val name: String,
    val description: String,
    val bedType: String,
    val bathroomType: String,
    val amenities: List<Amenity> = emptyList(),
    val smokingPolicy: Boolean,
    val petPolicy: Boolean
)

data class Amenity(
    val name: String,
    val iconUrl: String,
)