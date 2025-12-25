package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class UpdateBookingUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        booking: Booking,
        availableRooms: Int
    ): Booking {
        return repository.updateBooking(booking, availableRooms)
    }
}