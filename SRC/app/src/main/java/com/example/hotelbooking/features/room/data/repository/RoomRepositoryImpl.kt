package com.example.hotelbooking.features.room.data.repository

import com.example.hotelbooking.features.room.data.mapper.toRoomType
import com.example.hotelbooking.features.room.data.source.FirebaseRoomDataSource
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.repository.RoomRepository

class RoomRepositoryImpl(
    private val dataSource: FirebaseRoomDataSource
) : RoomRepository {

    override suspend fun getRoomsByHotelId(hotelId: String): List<RoomType> {
        return dataSource.fetchRoomsByHotelId(hotelId)
            .map { (id, dto) -> dto.toRoomType(id) }
    }

    override suspend fun getRoomById(roomId: String): RoomType? {
        return dataSource.fetchRoomById(roomId)?.toRoomType(roomId)
    }
}