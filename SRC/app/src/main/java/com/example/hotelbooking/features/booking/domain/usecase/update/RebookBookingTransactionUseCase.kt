package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import javax.inject.Inject

class RebookBookingTransactionUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: String, updatedBooking: Booking, newTransaction: Transaction): Result<Unit> {
        return repository.rebookTransaction(bookingId, updatedBooking, newTransaction)
    }
}