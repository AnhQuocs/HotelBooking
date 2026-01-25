package com.example.hotelbooking.features.transaction.domain.usecase

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

class CompleteBookingPaymentUseCase @Inject constructor(private val repository: BookingRepository) {
    suspend operator fun invoke(bookingId: String, transactionId: String) =
        repository.confirmBookingPayment(bookingId, transactionId)
}