package com.example.hotelbooking.features.booking.domain.usecase.delete

import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.booking.presentation.ui.history.toLocalDateTime
import java.time.LocalDateTime

class CancelBookingUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String, reason: CancelReason): Boolean {
        if (reason == CancelReason.TIMEOUT) {
            return repository.cancelBooking(bookingId, reason)
        }

        val booking = repository.getBookingById(bookingId)

        val now = LocalDateTime.now()
        val checkInTime = booking.startDate.toLocalDateTime()

        val cancellationDeadline = checkInTime.minusHours(24)

        return if (now.isBefore(cancellationDeadline)) {
            repository.cancelBooking(bookingId, reason)
        } else {
            false
        }
    }
}