package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository

class GetAllHotelsUseCase (
    private val repository: HotelRepository
) {
    suspend operator fun invoke(): List<Hotel> {
        return repository.getAllHotels()
    }
}