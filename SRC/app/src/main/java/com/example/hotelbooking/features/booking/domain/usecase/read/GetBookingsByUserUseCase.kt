package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class GetBookingsByUserUseCase (
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        userId: String
    ): List<Booking> {
        return repository.getBookingsByUser(userId)
    }
}