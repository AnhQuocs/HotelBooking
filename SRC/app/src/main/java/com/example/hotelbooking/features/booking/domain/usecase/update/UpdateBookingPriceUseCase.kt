package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

class UpdateBookingPriceUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        bookingId: String,
        discountAmount: Double,
        newPrice: Double,
    ): Result<Unit> {
        return repository.updateBookingPrice(bookingId, discountAmount, newPrice)
    }
}