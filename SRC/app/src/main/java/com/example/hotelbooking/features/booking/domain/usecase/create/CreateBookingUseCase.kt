package com.example.hotelbooking.features.booking.domain.usecase.create

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository

class CreateBookingUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        booking: Booking,
        availableRooms: Int
    ): Result<Booking> {
        return try {
            // 1. Validate Logic
            if (availableRooms <= 0) {
                return Result.failure(Exception("No rooms available, please choose another room."))
            }
            if (booking.totalPrice <= 0) {
                return Result.failure(Exception("Invalid total price."))
            }

            // 2. Call Repository
            val result = repository.createBooking(booking, availableRooms)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}