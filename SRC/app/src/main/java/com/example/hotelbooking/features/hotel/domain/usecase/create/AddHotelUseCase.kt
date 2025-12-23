package com.example.hotelbooking.features.hotel.domain.usecase.create

import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository

class AddHotelUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(adminHotel: AdminHotel) {
        return repository.addHotel(adminHotel)
    }
}