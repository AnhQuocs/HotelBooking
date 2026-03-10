package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookingDetailWithHotelUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(bookingId: String): Flow<BookingWithHotel?> {
        return bookingRepository.getBookingById(bookingId).flatMapLatest { booking ->
            if (booking == null) flowOf(null)
            else {
                hotelRepository.getHotelById(booking.hotelId).map { hotel ->
                    BookingWithHotel(booking, hotel)
                }
            }
        }
    }
}