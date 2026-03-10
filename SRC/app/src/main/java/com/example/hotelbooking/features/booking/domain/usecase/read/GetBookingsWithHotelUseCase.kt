package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookingsWithHotelUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId: String): Flow<List<BookingWithHotel>> {
        return bookingRepository.getBookingsByUser(userId).flatMapLatest { bookings ->
            if (bookings.isEmpty()) flowOf(emptyList())
            else {
                val flows = bookings.map { booking ->
                    hotelRepository.getHotelById(booking.hotelId).map { hotel ->
                        BookingWithHotel(booking, hotel)
                    }
                }
                combine(flows) { it.toList() }
            }
        }
    }
}