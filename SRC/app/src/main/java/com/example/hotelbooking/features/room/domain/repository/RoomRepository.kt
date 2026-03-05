package com.example.hotelbooking.features.room.domain.repository

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.RoomType
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun getRoomById(roomId: String): RoomType?
    fun getRoomTypesByHotel(hotelId: String): Flow<List<RoomType>>

    suspend fun getAdminRoomById(roomId: String): AdminRoomType?
    fun getAdminRoomTypeById(roomId: String): Flow<RoomType?>
    suspend fun addRoomType(roomType: AdminRoomType): Result<Unit>
    suspend fun updateRoomType(roomType: AdminRoomType): Result<Unit>
    suspend fun updateStatus(roomId: String, status: HotelStatus): Result<Unit>
}