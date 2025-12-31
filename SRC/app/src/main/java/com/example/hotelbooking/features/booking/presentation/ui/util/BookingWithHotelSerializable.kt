package com.example.hotelbooking.features.booking.presentation.ui.util

import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingWithHotel
import kotlinx.serialization.Serializable

@Serializable
data class BookingWithHotelSerializable(
    val hotelName: String,
    val hotelAddress: String,
    val pricePerNight: Double,
    val averageRating: Double,
    val bookingStart: Long,
    val bookingEnd: Long,
    val numberOfGuests: Int
)

fun BookingWithHotel.toSerializable(): BookingWithHotelSerializable {
    val hotel = this.hotel ?: throw IllegalStateException("Hotel is null")
    return BookingWithHotelSerializable(
        hotelName = hotel.name,
        hotelAddress = hotel.shortAddress,
        pricePerNight = hotel.pricePerNightMin.toDouble(),
        averageRating = hotel.averageRating,
        bookingStart = booking.startDate.seconds,
        bookingEnd = booking.endDate.seconds,
        numberOfGuests = booking.numberOfGuests
    )
}