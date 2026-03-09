package com.example.hotelbooking.features.booking.presentation.viewmodel.user

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.Guest
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.usecase.BookingUseCases
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentTimerManager
import com.example.hotelbooking.features.room.domain.model.RoomType
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
    data class Available(val roomNumbers: List<String>) : BookingUiState()
    data class SoldOut(val message: String) : BookingUiState()
    data class BookingSuccess(val booking: Booking) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingUseCases: BookingUseCases,
    private val timerManager: PaymentTimerManager
) : ViewModel() {

    private val _isTimeout = MutableStateFlow(false)
    val isTimeout = _isTimeout.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    fun onTimeout() {
        _isTimeout.value = true
    }

    var checkInDate by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var checkOutDate by mutableStateOf<LocalDate>(LocalDate.now().plusDays(1))
        private set

    private val _uiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    var currentAvailableRooms: Int = 0
        private set

    val timeLeft = timerManager.timeLeft

    fun startPaymentTimer(expireAt: Timestamp?, bookingId: String) {
        if (expireAt == null) {
            return
        }

        val serverTimeSec = expireAt.seconds
        val localTimeSec = System.currentTimeMillis() / 1000
        val remainingSeconds = serverTimeSec - localTimeSec


        if (remainingSeconds > 0) {
            timerManager.startTimer(bookingId, remainingSeconds.toInt())
        } else {
            _isTimeout.value = true
            timerManager.stopTimer()
            (timerManager.timeLeft as MutableStateFlow).value = 0
        }
    }

    fun stopPaymentTimer() {
        timerManager.stopTimer()
    }

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
        currentRoomType: RoomType,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading

            val allRoomNumbers = currentRoomType.roomList.map { it.roomNumber }

            val result = bookingUseCases.checkAvailabilityUseCase(
                hotelId,
                currentRoomType.id,
                allRoomNumbers,
                startDate,
                endDate
            )

            result.onSuccess { availableList ->

                val count = availableList.size
                currentAvailableRooms = count

                if (count > 0) {
                    _uiState.value = BookingUiState.Available(roomNumbers = availableList)
                } else {
                    _uiState.value = BookingUiState.SoldOut("Sorry, no rooms available for specific dates.")
                }

            }.onFailure { error ->
                _uiState.value = BookingUiState.Error(error.message ?: "Room inspection error")
            }
        }
    }

    fun submitBooking(
        hotelId: String,
        ownerId: String,
        roomTypeId: String,
        roomNumber: String,
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        guests: List<Guest>,
        numberOfGuests: Int,
        pricePerNight: Double,
        timeoutSeconds: Long
    ) {
        viewModelScope.launch {
            if (roomNumber.isBlank()) {
                _uiState.value = BookingUiState.Error(
                    "Please select a specific room number."
                )
                return@launch
            }

            _isSubmitting.value = true

            val totalDays = ChronoUnit.DAYS
                .between(startDate, endDate)
                .coerceAtLeast(1)

            val totalPrice = pricePerNight * totalDays

            val newBooking = Booking(
                bookingId = "",
                userId = userId,
                hotelId = hotelId,
                ownerId = ownerId,
                roomTypeId = roomTypeId,
                roomNumber = roomNumber,
                startDate = Timestamp(
                    startDate.atStartOfDay(ZoneOffset.UTC).toInstant()
                ),
                endDate = Timestamp(
                    endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                ),
                guests = guests,
                numberOfGuests = numberOfGuests,
                discountAmount = 0.0,
                totalPrice = totalPrice,
                status = BookingStatus.PENDING,
                stayStatus = StayStatus.NONE,
                cancelReason = null,
                createdAt = Timestamp.now(),
            )

            val result = bookingUseCases.createBookingUseCase(
                booking = newBooking,
                roomTypeId = roomTypeId,
                roomNumber = roomNumber,
                timeoutSeconds = timeoutSeconds
            )

            result
                .onSuccess { booking ->
                    _isSubmitting.value = false
                    _uiState.value =
                        BookingUiState.BookingSuccess(booking)
                }
                .onFailure { error ->
                    _isSubmitting.value = false
                    _uiState.value =
                        BookingUiState.Error(error.message ?: "Booking failed")
                }
        }
    }

    fun updateBookingPrice(
        bookingId: String,
        discountAmount: Double,
        newPrice: Double,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true

            val result = bookingUseCases.updateBookingPriceUseCase(bookingId, discountAmount, newPrice)

            result
                .onSuccess {
                    _isSubmitting.value = false
                    onComplete(true)
                }
                .onFailure { error ->
                    _isSubmitting.value = false
                    _uiState.value = BookingUiState.Error(error.message ?: "Lỗi cập nhật giá")
                    onComplete(false)
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

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}