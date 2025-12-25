package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import java.time.LocalDate

class GetBookingsUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        hotelId: String,
        roomTypeId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        statuses: List<BookingStatus> = listOf(BookingStatus.CONFIRMED)
    ): List<Booking> {
        return repository.getBookings(hotelId, roomTypeId, startDate, endDate, statuses)
    }
}