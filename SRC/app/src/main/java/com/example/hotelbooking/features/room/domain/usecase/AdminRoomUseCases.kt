package com.example.hotelbooking.features.room.domain.usecase

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.domain.usecase.update.SyncHotelMinPriceUseCase
import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminRoomUseCases @Inject constructor(
    val addRoomTypeUseCase: AddRoomTypeUseCase,
    val updateRoomTypeUseCase: UpdateRoomTypeUseCase,
    val getAdminRoomByIdUseCase: GetAdminRoomByIdUseCase,
    val syncHotelMinPriceUseCase: SyncHotelMinPriceUseCase,
    val getAdminRoomTypeByIdUseCase: GetAdminRoomTypeByIdUseCase,
    val updateStatusUseCase: UpdateStatusUseCase
)

class AddRoomTypeUseCase @Inject constructor(
    private val repository: RoomRepository,
    private val syncMinPriceUseCase: SyncHotelMinPriceUseCase
) {
    suspend operator fun invoke(roomType: AdminRoomType): Result<Unit> {
        val result = repository.addRoomType(roomType)

        if (result.isSuccess) {
            syncMinPriceUseCase(roomType.hotelId)
        }

        return result
    }
}

class UpdateRoomTypeUseCase @Inject constructor(
    private val repository: RoomRepository,
    private val syncMinPriceUseCase: SyncHotelMinPriceUseCase
) {
    suspend operator fun invoke(roomType: AdminRoomType): Result<Unit> {
        val result = repository.updateRoomType(roomType)

        if (result.isSuccess) {
            syncMinPriceUseCase(roomType.hotelId)
        }

        return result
    }
}

class GetAdminRoomByIdUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String): AdminRoomType? {
        return repository.getAdminRoomById(roomId)
    }
}

class GetAdminRoomTypeByIdUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    operator fun invoke(roomId: String): Flow<RoomType?> {
        return repository.getAdminRoomTypeById(roomId)
    }
}

class UpdateStatusUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String, status: HotelStatus): Result<Unit> {
        return repository.updateStatus(roomId, status)
    }
}