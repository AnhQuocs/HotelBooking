package com.example.hotelbooking.features.room.domain.repository

import com.example.hotelbooking.features.room.domain.model.RoomType

interface RoomRepository {
    suspend fun getRoomsByHotelId(hotelId: String): List<RoomType>
    suspend fun getRoomById(roomId: String): RoomType?
}