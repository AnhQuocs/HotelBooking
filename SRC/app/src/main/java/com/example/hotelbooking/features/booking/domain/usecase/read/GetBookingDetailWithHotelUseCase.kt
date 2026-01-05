package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import javax.inject.Inject

class GetBookingDetailWithHotelUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(bookingId: String): BookingWithHotel {
        val booking = bookingRepository.getBookingsById(bookingId)

        val hotel = hotelRepository.getHotelById(booking.hotelId)

        return BookingWithHotel(booking, hotel)
    }
}