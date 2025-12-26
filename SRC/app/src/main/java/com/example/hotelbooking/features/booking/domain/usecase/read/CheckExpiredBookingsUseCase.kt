package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

class CheckExpiredBookingsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(userId: String): Result<Int> {
        return repository.checkAndCancelExpiredBookings(userId)
    }
}