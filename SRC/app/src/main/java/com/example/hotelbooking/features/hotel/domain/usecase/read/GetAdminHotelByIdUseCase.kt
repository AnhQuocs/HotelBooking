package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import javax.inject.Inject

class GetAdminHotelByIdUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotelId: String): AdminHotel? {
        return repository.getAdminHotelById(hotelId)
    }
}