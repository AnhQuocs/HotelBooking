package com.example.hotelbooking.features.room.data.dto

import com.google.firebase.firestore.PropertyName

data class RoomTypeDto(
    val hotelId: String? = null,
    val totalStock: Int? = null,
    val roomList: List<RoomDto> = emptyList(),
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

data class RoomDto(
    @get:PropertyName("roomNumber")
    @set:PropertyName("roomNumber")
    var roomNumber: String? = null,

    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable: Boolean? = null
)