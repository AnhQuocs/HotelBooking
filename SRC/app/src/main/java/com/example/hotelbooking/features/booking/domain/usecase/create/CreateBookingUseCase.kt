package com.example.hotelbooking.features.booking.domain.usecase.create

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.google.firebase.Timestamp

class CreateBookingUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        booking: Booking,
        roomTypeId: String,
        roomNumber: String,
        timeoutSeconds: Long
    ): Result<Booking> {
        return try {
            if (booking.totalPrice <= 0) {
                return Result.failure(Exception("Invalid total price."))
            }

            val now = Timestamp.now()
            val expireAt = Timestamp(
                now.seconds + timeoutSeconds,
                0
            )

            val result = repository.createBooking(booking, roomTypeId, roomNumber, expireAt)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}