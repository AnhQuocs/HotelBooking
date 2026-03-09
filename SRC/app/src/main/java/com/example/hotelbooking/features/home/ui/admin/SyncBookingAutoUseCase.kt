package com.example.hotelbooking.features.home.ui.admin

import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.google.firebase.Timestamp
import javax.inject.Inject

class SyncBookingAutoUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(bookings: List<Booking>) {
        val now = Timestamp.now()

        bookings.forEach { booking ->
            val startDate = booking.startDate
            val endDate = booking.endDate

            val twelveHoursInSeconds = 12 * 60 * 60
            if (booking.stayStatus == StayStatus.NONE &&
                now.seconds > (startDate.seconds + twelveHoursInSeconds)
            ) {

                bookingRepository.updateBooking(
                    booking.copy(
                        stayStatus = StayStatus.NO_SHOW,
                        status = BookingStatus.CANCELLED,
                        cancelReason = CancelReason.OTHER,
                        cancelNote = "NO SHOW",
                        updatedAt = now
                    )
                )
            }

            if (booking.stayStatus == StayStatus.CHECK_IN &&
                now.seconds > endDate.seconds
            ) {

                bookingRepository.updateBooking(
                    booking.copy(
                        stayStatus = StayStatus.CHECK_OUT,
                        updatedAt = now
                    )
                )
            }
        }
    }
}