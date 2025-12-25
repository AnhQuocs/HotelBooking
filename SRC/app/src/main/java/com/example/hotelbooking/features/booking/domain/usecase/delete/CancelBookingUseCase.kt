package com.example.hotelbooking.features.booking.domain.usecase.delete

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class CancelBookingUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String): Boolean {
        return repository.cancelBooking(bookingId)
    }
}