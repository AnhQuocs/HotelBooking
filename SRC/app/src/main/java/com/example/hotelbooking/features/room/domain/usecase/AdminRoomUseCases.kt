package com.example.hotelbooking.features.room.domain.usecase

import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.repository.RoomRepository
import javax.inject.Inject

class AdminRoomUseCases @Inject constructor(
    val addRoomTypeUseCase: AddRoomTypeUseCase,
    val updateRoomTypeUseCase: UpdateRoomTypeUseCase,
    val getAdminRoomByIdUseCase: GetAdminRoomByIdUseCase
)

class AddRoomTypeUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomType: AdminRoomType): Result<Unit> {
        return repository.addRoomType(roomType)
    }
}

class UpdateRoomTypeUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomType: AdminRoomType): Result<Unit> {
        return repository.updateRoomType(roomType)
    }
}

class GetAdminRoomByIdUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String): AdminRoomType? {
        return repository.getAdminRoomById(roomId)
    }
}