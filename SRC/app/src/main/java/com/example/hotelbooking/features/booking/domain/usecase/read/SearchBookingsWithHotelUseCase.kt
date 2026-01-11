package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.utils.removeAccents
import javax.inject.Inject

class SearchBookingsWithHotelUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository
) {

    suspend operator fun invoke(userId: String, query: String): List<BookingWithHotel> {
        // Lấy toàn bộ booking của user
        val allBookings = bookingRepository.getBookingsByUser(userId)
        if (allBookings.isEmpty()) return emptyList()

        // Lấy danh sách hotelId duy nhất
        val hotelIds = allBookings.map { it.hotelId }.distinct()

        // Lấy thông tin hotel từ hotelRepository
        val hotelsMap = hotelIds.associateWith { id ->
            hotelRepository.getHotelById(id)
        }

        // Map
        val bookingsWithHotel = allBookings.map { booking ->
            BookingWithHotel(
                booking = booking,
                hotel = hotelsMap[booking.hotelId]
            )
        }

        // Chuẩn hóa query
        val normalizedQuery = query.lowercase().removeAccents()

        // Filter theo bookingId, hotelName, shortAddress
        return if (normalizedQuery.isBlank()) {
            bookingsWithHotel
        } else {
            bookingsWithHotel.filter { bwh ->
                val hotel = bwh.hotel
                val matchesBookingId = bwh.booking.bookingId.lowercase().removeAccents().contains(normalizedQuery)
                val matchesHotelName = hotel?.name?.lowercase()?.removeAccents()?.contains(normalizedQuery) == true
                val matchesShortAddress = hotel?.shortAddress?.lowercase()?.removeAccents()?.contains(normalizedQuery) == true

                matchesBookingId || matchesHotelName || matchesShortAddress
            }
        }
    }
}