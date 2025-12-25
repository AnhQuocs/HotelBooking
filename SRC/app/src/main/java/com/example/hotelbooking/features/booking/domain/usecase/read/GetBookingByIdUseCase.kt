package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class GetBookingByIdUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String): Booking {
        return repository.getBookingsById(bookingId)
    }
}