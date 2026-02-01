package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import java.time.LocalDate

class CheckAvailabilityUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        hotelId: String,
        roomTypeId: String,
        allRoomNumbers: List<String>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<String>> {
        if (startDate.isBefore(LocalDate.now())) {
            return Result.failure(Exception("Check-in date cannot be in the past"))
        }
        if (endDate.isBefore(startDate.plusDays(1))) {
            return Result.failure(Exception("Check-out date must be at least 1 day after check-in"))
        }

        return try {
            val availableRooms = repository.getAvailableRoomNumbers(
                hotelId, roomTypeId, allRoomNumbers, startDate, endDate
            )
            Result.success(availableRooms)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}