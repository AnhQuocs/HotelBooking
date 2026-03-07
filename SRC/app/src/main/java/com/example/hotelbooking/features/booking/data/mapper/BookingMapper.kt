package com.example.hotelbooking.features.booking.data.mapper

import com.example.hotelbooking.features.booking.data.dto.BookingDto
import com.example.hotelbooking.features.booking.data.dto.GuestDto
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.model.Guest
import com.example.hotelbooking.features.booking.domain.model.StayStatus

fun Booking.toDto() = BookingDto(
    bookingId = bookingId,
    userId = userId,
    hotelId = hotelId,
    roomTypeId = roomTypeId,
    roomNumber = roomNumber,
    startDate = startDate,
    endDate = endDate,
    guests = guests.map { it.toDto() },
    numberOfGuests = numberOfGuests,
    discountAmount = discountAmount,
    totalPrice = totalPrice,
    status = status.name,
    cancelReason = cancelReason?.name,
    cancelNote = cancelNote,
    stayStatus = stayStatus.name,
    createdAt = createdAt,
    expireAt = expireAt,
    updatedAt = updatedAt
)

fun Guest.toDto() = GuestDto(
    id = id,
    fullName = fullName,
    phone = phone,
    email = email,
    dayOfBirth = dateOfBirth,
    representative = isRepresentative
)

fun BookingDto.toDomain() = Booking(
    bookingId = bookingId,
    userId = userId,
    hotelId = hotelId,
    roomTypeId = roomTypeId,
    roomNumber = roomNumber,
    startDate = startDate,
    endDate = endDate,
    guests = guests.map { it.toDomain() },
    numberOfGuests = numberOfGuests,
    discountAmount = discountAmount,
    totalPrice = totalPrice,
    status = BookingStatus.valueOf(status),
    cancelReason = cancelReason?.let { CancelReason.valueOf(cancelReason) },
    cancelNote = cancelNote,
    stayStatus = StayStatus.valueOf(stayStatus),
    createdAt = createdAt,
    expireAt = expireAt,
    updatedAt = updatedAt
)

fun GuestDto.toDomain() = Guest(
    id = id,
    fullName = fullName,
    phone = phone,
    email = email,
    dateOfBirth = dayOfBirth,
    isRepresentative = representative
)