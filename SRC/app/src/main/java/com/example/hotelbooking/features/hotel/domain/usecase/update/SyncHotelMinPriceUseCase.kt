package com.example.hotelbooking.features.hotel.domain.usecase.update

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.features.room.domain.repository.RoomRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SyncHotelMinPriceUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(hotelId: String) {
        val rooms = roomRepository.getRoomTypesByHotel(hotelId).firstOrNull() ?: emptyList()

        val minPrice = rooms
            .filter { it.status == HotelStatus.ACTIVE }
            .minOfOrNull { it.pricePerNight.toDouble() } ?: 0.0

        hotelRepository.updateHotelMinPrice(hotelId, minPrice)
    }
}