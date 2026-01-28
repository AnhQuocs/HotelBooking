package com.example.hotelbooking.features.booking.presentation.ui.checkout

import com.example.hotelbooking.features.booking.di.ApplicationScope
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.usecase.update.CancelBookingAndTransactionUseCase
import com.example.hotelbooking.features.main.BookingRefreshEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentTimerManager @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val cancelBookingAndTransactionUseCase: CancelBookingAndTransactionUseCase
) {
    private val _timeLeft = MutableStateFlow<Long>(-1L)
    val timeLeft = _timeLeft.asStateFlow()

    private var expiryTime: Long = 0
    private var currentBookingId: String? = null
    private var timerJob: Job? = null

    fun startTimer(bookingId: String, durationSeconds: Int) {
        expiryTime = System.currentTimeMillis() + (durationSeconds * 1000L)
        currentBookingId = bookingId
        _timeLeft.value = durationSeconds.toLong()

        timerJob?.cancel()
        timerJob = scope.launch {
            while (System.currentTimeMillis() < expiryTime) {
                val remaining = (expiryTime - System.currentTimeMillis()) / 1000
                _timeLeft.value = remaining.coerceAtLeast(0L)
                delay(1000L)
            }
            _timeLeft.value = 0
            handleTimeout()
        }
    }

    private suspend fun handleTimeout() {
        currentBookingId?.let { id ->
            cancelBookingAndTransactionUseCase(
                bookingId = id,
                cancelReason = CancelReason.TIMEOUT.name
            )
            BookingRefreshEvent.triggerRefresh()
            currentBookingId = null
        }
    }

    fun isRunning() = _timeLeft.value > 0

    fun stopTimer() {
        timerJob?.cancel()
        _timeLeft.value = -1L
        currentBookingId = null
    }
}