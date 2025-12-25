package com.example.hotelbooking.features.booking.domain.usecase.update

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class ExpirePendingBookingsUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke() = repository.expirePendingBookings()
}