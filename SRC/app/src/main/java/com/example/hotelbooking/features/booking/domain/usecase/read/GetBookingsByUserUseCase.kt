package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookingsByUserUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    operator fun invoke(userId: String): Flow<List<Booking>> {
        return repository.getBookingsByUser(userId)
    }
}