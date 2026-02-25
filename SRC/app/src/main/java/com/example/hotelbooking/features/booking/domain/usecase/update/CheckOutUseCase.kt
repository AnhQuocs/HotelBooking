package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.google.firebase.Timestamp
import javax.inject.Inject

class CheckOutUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(booking: Booking): Boolean {
        val today = Timestamp.now()

        val isEarlyCheckOut = today.seconds < booking.endDate.seconds

        val updatedBooking = booking.copy(
            stayStatus = StayStatus.CHECK_OUT,
            endDate = if (isEarlyCheckOut) today else booking.endDate
        )

        return bookingRepository.updateBooking(updatedBooking)
    }
}