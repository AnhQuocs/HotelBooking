package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class UpdateStatusUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String, status: BookingStatus): Booking {
        return repository.updateStatus(bookingId, status)
    }
}