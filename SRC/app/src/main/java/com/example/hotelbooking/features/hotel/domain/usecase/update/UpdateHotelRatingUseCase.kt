package com.example.hotelbooking.features.hotel.domain.usecase.update

import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository

class UpdateHotelRatingUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(
        hotelId: String,
        rating: Double
    ) {
        repository.updateHotelRating(hotelId, rating)
    }
}