package com.example.hotelbooking.features.booking.domain.usecase

import com.example.hotelbooking.features.booking.domain.usecase.create.CreateBookingUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.CheckAvailabilityUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingByIdUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingsByUserUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingsUseCase
import com.example.hotelbooking.features.booking.domain.usecase.delete.CancelBookingUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.CheckExpiredBookingsUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.ExpirePendingBookingsUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateBookingUseCase

data class BookingUseCases(
    val checkAvailabilityUseCase: CheckAvailabilityUseCase,
    val createBookingUseCase: CreateBookingUseCase,
    val cancelBookingUseCase: CancelBookingUseCase,
    val updateBookingUseCase: UpdateBookingUseCase,
    val getBookingsByUserUseCase: GetBookingsByUserUseCase,
    val getBookingByIdUseCase: GetBookingByIdUseCase,
    val getBookingsUseCase: GetBookingsUseCase,
    val expirePendingBookingsUseCase: ExpirePendingBookingsUseCase,
    val checkExpirePendingBookingsUseCase: CheckExpiredBookingsUseCase
)