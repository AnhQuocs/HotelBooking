package com.example.hotelbooking.features.booking.data.dto

import com.google.firebase.Timestamp

data class BookingDto(
    val bookingId: String = "",
    val userId: String = "",
    val hotelId: String = "",
    val roomTypeId: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val guest: GuestDto = GuestDto(),
    val numberOfGuests: Int = 0,
    val totalPrice: Double = 0.0,
    val status: String = "PENDING",
    val createdAt: Timestamp = Timestamp.now()
)

data class GuestDto(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val age: Int = 0
)