package com.example.hotelbooking.features.booking.data.dto

import com.google.firebase.Timestamp

data class BookingDto(
    val bookingId: String = "",
    val userId: String = "",
    val hotelId: String = "",
    val roomTypeId: String = "",
    val roomNumber: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val guests: List<GuestDto> = emptyList(),
    val numberOfGuests: Int = 0,
    val discountAmount: Double = 0.0,
    val totalPrice: Double = 0.0,
    val status: String = "PENDING",
    val cancelReason: String? = null,
    val cancelNote: String? = "",
    val stayStatus: String = "NONE",
    val createdAt: Timestamp = Timestamp.now(),
    val expireAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
)

data class GuestDto(
    val id: String = "",
    val fullName: String = "",
    val phone: String? = null,
    val email: String? = null,
    val dayOfBirth: Timestamp? = null,
    val representative: Boolean = false
)