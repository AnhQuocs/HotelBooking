package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import javax.inject.Inject

class SearchHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(query: String): List<Hotel> {
        return repository.searchHotels(query)
    }
}