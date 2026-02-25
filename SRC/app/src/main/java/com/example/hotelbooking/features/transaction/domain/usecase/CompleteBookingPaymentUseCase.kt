package com.example.hotelbooking.features.transaction.domain.usecase

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentBrand
import javax.inject.Inject

class CompleteBookingPaymentUseCase @Inject constructor(private val repository: BookingRepository) {
    suspend operator fun invoke(bookingId: String, transactionId: String, brand: PaymentBrand) =
        repository.confirmBookingPayment(bookingId, transactionId, brand)
}