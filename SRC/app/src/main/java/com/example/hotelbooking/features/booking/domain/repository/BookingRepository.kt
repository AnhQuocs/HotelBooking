package com.example.hotelbooking.features.booking.domain.repository

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.transaction.domain.model.Transaction
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

    suspend fun createBooking(
        booking: Booking,
        roomTypeId: String,
        roomNumber: String,
        expireAt: Timestamp
    ): Booking

    suspend fun updateBooking(booking: Booking): Boolean

    suspend fun updateStayStatus(bookingId: String, newStatus: StayStatus): Booking

    suspend fun getBookingsByUser(userId: String): List<Booking>

    suspend fun getBookingById(bookingId: String): Booking

    suspend fun getBookings(
        hotelId: String,
        roomTypeId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        statuses: List<BookingStatus> = listOf(BookingStatus.CONFIRMED)
    ): List<Booking>

    suspend fun expirePendingBookings()

    suspend fun checkAndCancelExpiredBookings(userId: String): Result<Int>

    suspend fun confirmBookingPayment(bookingId: String, transactionId: String): Result<Unit>

    suspend fun cancelBookingAndTransaction(
        bookingId: String,
        cancelReason: String,
        cancelNote: String?
    ): Result<Unit>

    suspend fun rebookTransaction(
        bookingId: String,
        updatedBooking: Booking,
        newTransaction: Transaction
    ): Result<Unit>

    fun clearCache(userId: String)
}