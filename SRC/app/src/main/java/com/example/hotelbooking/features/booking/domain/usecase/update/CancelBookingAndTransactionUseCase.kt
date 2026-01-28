package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.booking.presentation.ui.history.toLocalDateTime
import java.time.LocalDateTime
import javax.inject.Inject

sealed class CancellationResult {
    object Success : CancellationResult()
    object TooLate : CancellationResult()
    data class Failure(val exception: Exception? = null) : CancellationResult()
}

class CancelBookingAndTransactionUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String, cancelReason: String): CancellationResult {
        return try {
            val booking = repository.getBookingById(bookingId)

            when (booking.status) {
                BookingStatus.PENDING -> {
                    performCancellation(bookingId, cancelReason)
                }

                BookingStatus.CONFIRMED -> {
                    val now = LocalDateTime.now()
                    val checkInTime = booking.startDate.toLocalDateTime()
                    val cancellationDeadline = checkInTime.minusHours(24)

                    if (now.isBefore(cancellationDeadline)) {
                        performCancellation(bookingId, cancelReason)
                    } else {
                        CancellationResult.TooLate
                    }
                }

                else -> {
                    CancellationResult.Failure(Exception("Invalid status for cancellation"))
                }
            }
        } catch (e: Exception) {
            CancellationResult.Failure(e)
        }
    }

    private suspend fun performCancellation(id: String, reason: String): CancellationResult {
        val result = repository.cancelBookingAndTransaction(id, reason)
        return if (result.isSuccess) {
            CancellationResult.Success
        } else {
            CancellationResult.Failure(result.exceptionOrNull() as? Exception)
        }
    }
}