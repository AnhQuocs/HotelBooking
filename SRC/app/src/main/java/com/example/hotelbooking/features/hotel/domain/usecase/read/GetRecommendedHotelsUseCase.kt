package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository

class GetRecommendedHotelsUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(minAverageRating: Double): List<Hotel> {
        return repository.getRecommendHotels(minAverageRating)
    }
}