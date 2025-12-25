package com.example.hotelbooking.features.booking.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.Guest
import com.example.hotelbooking.features.booking.domain.usecase.BookingUseCases
import com.example.hotelbooking.features.room.presentation.ui.toLocalDate
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    data class Available(val count: Int) : BookingUiState()
    data class SoldOut(val message: String) : BookingUiState()
    data class BookingSuccess(val bookingId: String) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

@HiltViewModel
class BookingViewModel @Inject constructor (
    private val bookingUseCases: BookingUseCases,
//    private val notificationUseCases: NotificationUseCases,
//    private val notificationHelper: NotificationHelper
) : ViewModel() {

    var checkInDate by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var checkOutDate by mutableStateOf<LocalDate>(LocalDate.now().plusDays(1))
        private set

    private val _uiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    var currentAvailableRooms: Int = 0
        private set

    fun onDateSelected(startMillis: Long?, endMillis: Long?) {
        if (startMillis != null) {
            checkInDate = startMillis.toLocalDate()
        }
        if (endMillis != null) {
            checkOutDate = endMillis.toLocalDate()
        }
        resetState()
    }

    fun checkRoomAvailability(
        hotelId: String,
        roomTypeId: String,
        totalStock: Int
    ) {
        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading

            val result = bookingUseCases.checkAvailabilityUseCase(
                hotelId, roomTypeId, totalStock, checkInDate, checkOutDate
            )

            result.onSuccess { count ->
                currentAvailableRooms = count
                if (count > 0) {
                    _uiState.value = BookingUiState.Available(count)
                } else {
                    _uiState.value = BookingUiState.SoldOut("Sorry, there are no rooms available on this date.")
                }
            }.onFailure { error ->
                _uiState.value = BookingUiState.Error(error.message ?: "Room inspection error")
            }
        }
    }

    fun submitBooking(
        hotelId: String,
        roomTypeId: String,
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        guest: Guest,
        numberOfGuests: Int,
        pricePerNight: Double,
        availableRooms: Int
    ) {
        viewModelScope.launch {
            if (availableRooms <= 0) {
                _uiState.value = BookingUiState.Error("Please check the date again, the room is fully booked.")
                return@launch
            }

            _uiState.value = BookingUiState.Loading

            // Tính tổng tiền
            val totalDays = ChronoUnit.DAYS.between(startDate, endDate).coerceAtLeast(1)
            val totalPrice = pricePerNight * totalDays

            val newBooking = Booking(
                bookingId = "",
                guestId = userId,
                hotelId = hotelId,
                roomTypeId = roomTypeId,
                startDate = Timestamp(startDate.atStartOfDay(ZoneOffset.UTC).toInstant()),
                endDate = Timestamp(endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()),
                guest = guest,
                numberOfGuests = numberOfGuests,
                totalPrice = totalPrice,
                status = BookingStatus.PENDING,
                createdAt = Timestamp.now()
            )

            val result = bookingUseCases.createBookingUseCase(newBooking, availableRooms)

            result.onSuccess { booking ->
                _uiState.value = BookingUiState.BookingSuccess(booking.bookingId)
            }.onFailure { error ->
                _uiState.value = BookingUiState.Error(error.message ?: "Booking failed")
            }
        }
    }

    fun updateStatus(bookingId: String, status: BookingStatus, hotelName: String = "") {
        viewModelScope.launch {
            try {
                _uiState.value = BookingUiState.Loading

                val updatedBooking = bookingUseCases.updateStatusUseCase(bookingId, status)

                if (status == BookingStatus.CONFIRMED) {
                    val title = "Booking Successful! 🎉"
                    val message = "You have successfully booked a room at $hotelName. Code: ${updatedBooking.bookingId}"

//                    notificationUseCases.saveNotificationUseCase(
//                        title = title,
//                        message = message,
//                        bookingId = updatedBooking.bookingId
//                    )
//
//                    notificationHelper.showBookingNotification(
//                        title = title,
//                        message = message,
//                        bookingId = updatedBooking.bookingId
//                    )
                }

                _uiState.value = BookingUiState.BookingSuccess(updatedBooking.bookingId)

            } catch (e: Exception) {
                _uiState.value = BookingUiState.Error(
                    e.message ?: "Failed to update booking status"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = BookingUiState.Idle
        currentAvailableRooms = 0
    }

    fun calculateTotalPrice(pricePerNight: Int): Long {
        if (_uiState.value !is BookingUiState.Available) return 0L

        val days = ChronoUnit.DAYS.between(checkInDate, checkOutDate)
        return if (days > 0) days * pricePerNight else 0L
    }
}