package com.example.hotelbooking.features.booking.domain.model

import com.google.firebase.Timestamp
import java.util.UUID

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED
}

enum class CancelReason {
    CHANGE_PLAN,
    WRONG_INFO,
    PAYMENT_ISSUE,
    FIND_BETTER_PRICE,
    SYSTEM_ERROR,
    OTHER,
    TIMEOUT
}

enum class StayStatus {
    NONE, NO_SHOW, CHECK_IN, CHECK_OUT
}

data class Booking(
    val bookingId: String,
    val userId: String,
    val hotelId: String,
    val roomTypeId: String,
    val roomNumber: String,
    val startDate: Timestamp,
    val endDate: Timestamp,
    val guests: List<Guest> = emptyList(),
    val numberOfGuests: Int,
    val discountAmount: Double,
    val totalPrice: Double,
    val status: BookingStatus,
    val cancelReason: CancelReason?,
    val cancelNote: String? = null,
    val stayStatus: StayStatus,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val expireAt: Timestamp? = null
)

data class Guest(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    val dateOfBirth: Timestamp? = null,
    val isRepresentative: Boolean = false
)