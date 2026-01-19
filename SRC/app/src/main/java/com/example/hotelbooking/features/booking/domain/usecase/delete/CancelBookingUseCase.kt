package com.example.hotelbooking.features.booking.domain.usecase.delete

import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.booking.presentation.ui.history.toLocalDateTime
import java.time.LocalDateTime
import javax.inject.Inject

sealed class CancellationResult {
    object Success : CancellationResult()
    object TooLate : CancellationResult()
    data class Failure(val exception: Exception? = null) : CancellationResult()
}

class CancelBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String, reason: CancelReason): CancellationResult {
        if (reason == CancelReason.TIMEOUT) {
            val success = repository.cancelBooking(bookingId, reason)
            return if (success) CancellationResult.Success else CancellationResult.Failure()
        }

        val booking = repository.getBookingById(bookingId)
        val now = LocalDateTime.now()
        val checkInTime = booking.startDate.toLocalDateTime()
        val cancellationDeadline = checkInTime.minusHours(24)

        return if (now.isBefore(cancellationDeadline)) {
            val success = repository.cancelBooking(bookingId, reason)
            if (success) CancellationResult.Success else CancellationResult.Failure()
        } else {
            CancellationResult.TooLate
        }
    }
}