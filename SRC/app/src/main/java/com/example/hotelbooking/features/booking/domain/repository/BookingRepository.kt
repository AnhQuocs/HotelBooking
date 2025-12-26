package com.example.hotelbooking.features.booking.domain.repository

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.google.firebase.Timestamp
import java.time.LocalDate

interface BookingRepository {
    suspend fun checkAvailability(
        hotelId: String,
        roomTypeId: String,
        totalRoom: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int

    suspend fun createBooking(booking: Booking, availableRooms: Int, expireAt: Timestamp): Booking

    suspend fun cancelBooking(bookingId: String): Boolean

    suspend fun updateBooking(booking: Booking, availableRooms: Int): Booking

    suspend fun updateStatus(bookingId: String, status: BookingStatus): Booking

    suspend fun getBookingsByUser(userId: String): List<Booking>

    suspend fun getBookingsById(bookingId: String): Booking

    suspend fun getBookings(
        hotelId: String,
        roomTypeId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        statuses: List<BookingStatus> = listOf(BookingStatus.CONFIRMED)
    ): List<Booking>

    suspend fun expirePendingBookings()

    suspend fun checkAndCancelExpiredBookings(userId: String): Result<Int>
}