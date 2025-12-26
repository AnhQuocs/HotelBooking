package com.example.hotelbooking.features.booking.data.mapper

import com.example.hotelbooking.features.booking.data.dto.BookingDto
import com.example.hotelbooking.features.booking.data.dto.GuestDto
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.Guest

fun Booking.toDto() = BookingDto(
    bookingId = bookingId,
    userId = userId,
    hotelId = hotelId,
    roomTypeId = roomTypeId,
    startDate = startDate,
    endDate = endDate,
    guest = guest.toDto(),
    numberOfGuests = numberOfGuests,
    totalPrice = totalPrice,
    status = status.name,
    createdAt = createdAt,
    expireAt = expireAt
)

fun Guest.toDto() = GuestDto(name, phone, email, age)

fun BookingDto.toDomain() = Booking(
    bookingId = bookingId,
    userId = userId,
    hotelId = hotelId,
    roomTypeId = roomTypeId,
    startDate = startDate,
    endDate = endDate,
    guest = guest.toDomain(),
    numberOfGuests = numberOfGuests,
    totalPrice = totalPrice,
    status = BookingStatus.valueOf(status),
    createdAt = createdAt,
    expireAt = expireAt
)

fun GuestDto.toDomain() = Guest(name, phone, email, age)