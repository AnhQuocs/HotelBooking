package com.example.hotelbooking.features.booking.domain.model

import com.example.hotelbooking.features.hotel.domain.model.Hotel

data class BookingWithHotel(
    val booking: Booking,
    val hotel: Hotel?
)