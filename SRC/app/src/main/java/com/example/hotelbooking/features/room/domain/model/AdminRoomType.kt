package com.example.hotelbooking.features.room.domain.model

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus

data class AdminRoomType(
    val id: String,
    val hotelId: String,
    val totalRoom: Int,
    val roomList: List<Room>,
    val pricePerNight: Int,
    val capacity: Int,
    val roomSize: Int,
    val imageUrl: String,
    val name: Map<String, String>,
    val description: Map<String, String>,
    val bedType: Map<String, String>,
    val bathroomType: Map<String, String>,
    val amenities: List<AdminAmenity> = emptyList(),
    val smokingPolicy: Boolean,
    val petPolicy: Boolean,
    val status: HotelStatus = HotelStatus.ACTIVE
)

data class AdminAmenity(
    val name: Map<String, String>,
    val iconUrl: String
)