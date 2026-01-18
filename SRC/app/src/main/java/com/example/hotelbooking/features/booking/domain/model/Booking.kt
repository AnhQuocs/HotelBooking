package com.example.hotelbooking.features.booking.domain.model

import com.google.firebase.Timestamp

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED
}

enum class CancelReason {
    USER, TIMEOUT
}

enum class StayStatus {
    NONE, NO_SHOW, CHECK_IN, CHECK_OUT
}

data class Booking(
    val bookingId: String,
    val userId: String,
    val hotelId: String,
    val roomTypeId: String,
    val startDate: Timestamp,
    val endDate: Timestamp,
    val guest: Guest,
    val numberOfGuests: Int,
    val totalPrice: Double,
    val status: BookingStatus,
    val cancelReason: CancelReason?,
    val stayStatus: StayStatus,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val expireAt: Timestamp? = null
)

data class Guest(
    val name: String,
    val phone: String,
    val email: String,
    val age: Int
)