package com.example.hotelbooking.features.hotel.domain.usecase.update

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import javax.inject.Inject

class UpdateHotelStatusUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotelId: String, status: HotelStatus) {
        repository.updateHotelStatus(hotelId, status)
    }
}