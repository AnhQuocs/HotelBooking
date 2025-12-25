package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import java.time.LocalDate

class CheckAvailabilityUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        hotelId: String,
        roomTypeId: String,
        totalRoom: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Int> { // Trả về Result thay vì Int trần
        // 1. Validate Business Rule
        if (startDate.isBefore(LocalDate.now())) {
            return Result.failure(Exception("Check-in date cannot be in the past"))
        }
        if (endDate.isBefore(startDate.plusDays(1))) {
            return Result.failure(Exception("Check-out date must be at least 1 day after check-in"))
        }

        // 2. Call Repo
        return try {
            val count = repository.checkAvailability(hotelId, roomTypeId, totalRoom, startDate, endDate)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}