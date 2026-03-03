package com.example.hotelbooking.features.room.domain.usecase

import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow

data class RoomUseCases(
    val getRoomsByHotelIdUseCase: GetRoomsByHotelIdUseCase,
    val getRoomByIdUseCase: GetRoomByIdUseCase
)

class GetRoomsByHotelIdUseCase(
    private val repository: RoomRepository
) {
    operator fun invoke(hotelId: String): Flow<List<RoomType>> {
        return repository.getRoomTypesByHotel(hotelId)
    }
}

class GetRoomByIdUseCase (
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String): RoomType? {
        return repository.getRoomById(roomId)
    }
}