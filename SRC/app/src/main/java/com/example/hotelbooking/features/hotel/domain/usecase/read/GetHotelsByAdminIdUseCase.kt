package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository

class GetHotelsByAdminIdUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(adminId: String): List<Hotel> {
        return repository.getHotelsByAdminId(adminId)
    }
}