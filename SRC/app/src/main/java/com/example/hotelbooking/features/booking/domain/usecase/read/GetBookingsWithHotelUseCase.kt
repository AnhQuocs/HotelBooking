package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetBookingsWithHotelUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(userId: String): List<BookingWithHotel> {
        val bookings = bookingRepository.getBookingsByUser(userId)
        val hotelIds = bookings.map { it.hotelId }.distinct()

        val hotelsMap = hotelIds.associateWith { id ->
            hotelRepository.getHotelById(id).firstOrNull()
        }

        return bookings.map { booking ->
            BookingWithHotel(
                booking = booking,
                hotel = hotelsMap[booking.hotelId]
            )
        }
    }
}