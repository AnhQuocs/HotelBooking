package com.example.hotelbooking.features.booking.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.home.admin.ui.dashboard.BookingFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AdminBookingListViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings = _bookings.asStateFlow()

    fun loadFilteredBookings(
        hotelId: String,
        filterType: BookingFilterType,
        targetDateEpoch: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val targetDate = LocalDate.ofEpochDay(targetDateEpoch)

                val allBookings = bookingRepository.getAllBookingsByHotelId(hotelId)

                val filteredList = allBookings.filter { booking ->
                    val startDate =
                        booking.startDate.toDate().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    val endDate =
                        booking.endDate.toDate().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    val createdAt =
                        booking.createdAt.toDate().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate()

                    when (filterType) {
                        BookingFilterType.ARRIVALS -> {
                            startDate == targetDate && (booking.status == BookingStatus.CONFIRMED)
                        }

                        BookingFilterType.DEPARTURES -> {
                            endDate == targetDate && (booking.status == BookingStatus.CONFIRMED)
                        }

                        BookingFilterType.NEW_BOOKINGS -> {
                            createdAt == targetDate
                        }

                        BookingFilterType.OCCUPANCY -> {
                            !targetDate.isBefore(startDate) && targetDate.isBefore(endDate) &&
                                    (booking.status == BookingStatus.CONFIRMED)
                        }

                        BookingFilterType.REVENUE -> {
                            createdAt == targetDate && (booking.status == BookingStatus.CONFIRMED)
                        }
                    }
                }

                _bookings.value = filteredList.sortedByDescending { it.createdAt }

            } catch (e: Exception) {
                _bookings.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}