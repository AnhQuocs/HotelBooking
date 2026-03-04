package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow

class GetHotelByIdUseCase(
    private val repository: HotelRepository
) {
    operator fun invoke(hotelId: String): Flow<Hotel?> {
        return repository.getHotelById(hotelId)
    }
}