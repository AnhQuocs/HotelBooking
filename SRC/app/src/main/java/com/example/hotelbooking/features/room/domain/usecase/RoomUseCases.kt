package com.example.hotelbooking.features.room.domain.usecase

import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.repository.RoomRepository

data class RoomUseCases(
    val getRoomsByHotelIdUseCase: GetRoomsByHotelIdUseCase,
    val getRoomByIdUseCase: GetRoomByIdUseCase
)

class GetRoomsByHotelIdUseCase(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(hotelId: String): List<RoomType> {
        return repository.getRoomsByHotelId(hotelId)
    }
}

class GetRoomByIdUseCase (
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String): RoomType? {
        return repository.getRoomById(roomId)
    }
}