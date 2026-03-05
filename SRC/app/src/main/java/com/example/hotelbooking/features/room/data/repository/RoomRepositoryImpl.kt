package com.example.hotelbooking.features.room.data.repository

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.room.data.mapper.toAdminRoomType
import com.example.hotelbooking.features.room.data.mapper.toDto
import com.example.hotelbooking.features.room.data.mapper.toRoomType
import com.example.hotelbooking.features.room.data.source.FirebaseRoomDataSource
import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomRepositoryImpl(
    private val dataSource: FirebaseRoomDataSource
) : RoomRepository {
    override suspend fun getRoomById(roomId: String): RoomType? {
        return dataSource.fetchRoomById(roomId)?.toRoomType(roomId)
    }

    override suspend fun addRoomType(roomType: AdminRoomType): Result<Unit> {
        return dataSource.addRoomType(roomType.toDto())
    }

    override suspend fun updateRoomType(roomType: AdminRoomType): Result<Unit> {
        return dataSource.updateRoomType(id = roomType.id, roomType.toDto())
    }

    override fun getRoomTypesByHotel(
        hotelId: String
    ): Flow<List<RoomType>> {

        return dataSource
            .observeRoomsByHotelId(hotelId)
            .map { list ->
                list.map { (id, dto) ->
                    dto.toRoomType(id)
                }
            }
    }

    override suspend fun getAdminRoomById(roomId: String): AdminRoomType? {
        return dataSource.fetchRoomById(roomId)?.toAdminRoomType(roomId)
    }

    override fun getAdminRoomTypeById(roomId: String): Flow<RoomType?> {
        return dataSource.observeRoomById(roomId).map { dto ->
            dto?.toRoomType(roomId)
        }
    }

    override suspend fun updateStatus(roomId: String, status: HotelStatus): Result<Unit> {
        return dataSource.updateRoomStatus(roomId, status.name)
    }
}