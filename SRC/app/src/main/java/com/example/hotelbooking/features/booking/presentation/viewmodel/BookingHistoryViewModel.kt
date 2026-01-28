package com.example.hotelbooking.features.booking.presentation.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.usecase.BookingUseCases
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingDetailWithHotelUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingsWithHotelUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.CancelBookingAndTransactionUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.CancellationResult
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateStayStatusUseCase
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentTimerManager
import com.example.hotelbooking.features.notification.domain.usecase.NotificationUseCases
import com.example.hotelbooking.features.notification.util.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingHistoryState<out T> {
    object Idle : BookingHistoryState<Nothing>()
    object Loading : BookingHistoryState<Nothing>()
    data class Success<T>(val data: T) : BookingHistoryState<T>()

    data class Error(
        @StringRes val messageRes: Int,
        val fallbackMessage: String? = null
    ) : BookingHistoryState<Nothing>()
}

@HiltViewModel
class BookingHistoryViewModel @Inject constructor(
    private val updateStayStatusUseCase: UpdateStayStatusUseCase,
    private val getBookingsWithHotelUseCase: GetBookingsWithHotelUseCase,
    private val getBookingDetailWithHotelUseCase: GetBookingDetailWithHotelUseCase,
    private val cancelBookingAndTransactionUseCase: CancelBookingAndTransactionUseCase,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper,
    private val timerManager: PaymentTimerManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state =
        MutableStateFlow<BookingHistoryState<List<BookingWithHotel>>>(BookingHistoryState.Loading)
    val state = _state.asStateFlow()

    private val _bookingDetailState =
        MutableStateFlow<BookingHistoryState<BookingWithHotel>>(BookingHistoryState.Loading)
    val bookingDetailState = _bookingDetailState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _isCancelling = MutableStateFlow(false)
    val isCancelling = _isCancelling.asStateFlow()

    val timeLeft = timerManager.timeLeft

    fun startPaymentTimer(bookingId: String, duration: Int) {
        if (!timerManager.isRunning()) {
            timerManager.startTimer(bookingId, duration)
        }
    }

    fun updateStayStatus(bookingId: String, newStatus: StayStatus) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                updateStayStatusUseCase(bookingId, newStatus)
                val updatedCombined = getBookingDetailWithHotelUseCase(bookingId)
                _bookingDetailState.value = BookingHistoryState.Success(updatedCombined)
            } catch (e: Exception) {
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun loadMyBookings(userId: String) {
        viewModelScope.launch {
            _state.value = BookingHistoryState.Loading
            try {
                val combinedList = getBookingsWithHotelUseCase(userId)
                _state.value = BookingHistoryState.Success(combinedList)
            } catch (e: Exception) {
                _state.value = BookingHistoryState.Error(
                    messageRes = R.string.error_unknown,
                    fallbackMessage = e.message
                )
            }
        }
    }

    fun loadBookingById(bookingId: String) {
        viewModelScope.launch {
            _bookingDetailState.value = BookingHistoryState.Loading
            try {
                val combined = getBookingDetailWithHotelUseCase(bookingId)
                _bookingDetailState.value = BookingHistoryState.Success(combined)
            } catch (e: Exception) {
                _bookingDetailState.value = BookingHistoryState.Error(
                    messageRes = R.string.error_unknown,
                    fallbackMessage = e.message
                )
            }
        }
    }

    suspend fun cancelBooking(
        bookingId: String,
        reason: CancelReason,
        cancelNote: String? = null,
        title: String? = null,
        message: String? = null
    ): Boolean {
        return try {
            _isCancelling.value = true

            val result = cancelBookingAndTransactionUseCase(
                bookingId = bookingId,
                cancelReason = reason.name,
                cancelNote = cancelNote
            )

            val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

            when (result) {
                is CancellationResult.Success -> {
                    if (reason != CancelReason.TIMEOUT && title != null && message != null) {
                        notificationUseCases.saveNotificationUseCase(
                            userId,
                            title,
                            message,
                            bookingId
                        )
                        notificationHelper.showBookingNotification(
                            title,
                            message,
                            bookingId
                        )
                    }

                    timerManager.stopTimer()
                    _bookingDetailState.value = BookingHistoryState.Idle
                    true
                }

                is CancellationResult.TooLate -> {
                    _bookingDetailState.value = BookingHistoryState.Error(
                        messageRes = R.string.error_cancel_too_late
                    )
                    false
                }

                is CancellationResult.Failure -> {
                    _bookingDetailState.value = BookingHistoryState.Error(
                        messageRes = R.string.error_cancel_failed,
                        fallbackMessage = result.exception?.message
                    )
                    false
                }
            }
        } catch (e: Exception) {
            _bookingDetailState.value = BookingHistoryState.Error(
                messageRes = R.string.error_unknown,
                fallbackMessage = e.message
            )
            false
        } finally {
            _isCancelling.value = false
        }
    }

    fun resetState() {
        _bookingDetailState.value = BookingHistoryState.Idle
    }
}