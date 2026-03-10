package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.utils.removeAccents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchBookingsWithHotelUseCase @Inject constructor(
    private val getBookingsWithHotelUseCase: GetBookingsWithHotelUseCase
) {
    operator fun invoke(userId: String, query: String): Flow<List<BookingWithHotel>> {
        val normalizedQuery = query.lowercase().removeAccents()

        return getBookingsWithHotelUseCase(userId).map { list ->
            if (normalizedQuery.isBlank()) {
                list
            } else {
                list.filter { bwh ->
                    val hotel = bwh.hotel
                    val matchesId = bwh.booking.bookingId.lowercase().contains(normalizedQuery)
                    val matchesName =
                        hotel?.name?.lowercase()?.removeAccents()?.contains(normalizedQuery) == true
                    val matchesAddress = hotel?.shortAddress?.lowercase()?.removeAccents()
                        ?.contains(normalizedQuery) == true

                    matchesId || matchesName || matchesAddress
                }
            }
        }
    }
}