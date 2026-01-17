package com.example.hotelbooking.features.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingDetailWithHotelUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingsWithHotelUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateStayStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingHistoryState<out T> {
    object Loading : BookingHistoryState<Nothing>()
    data class Success<T>(val data: T) : BookingHistoryState<T>()
    data class Error(val message: String) : BookingHistoryState<Nothing>()
}

@HiltViewModel
class BookingHistoryViewModel @Inject constructor(
    private val updateStayStatusUseCase: UpdateStayStatusUseCase,
    private val getBookingsWithHotelUseCase: GetBookingsWithHotelUseCase,
    private val getBookingDetailWithHotelUseCase: GetBookingDetailWithHotelUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<BookingHistoryState<List<BookingWithHotel>>>(BookingHistoryState.Loading)
    val state = _state.asStateFlow()

    private val _bookingDetailState =
        MutableStateFlow<BookingHistoryState<BookingWithHotel>>(BookingHistoryState.Loading)
    val bookingDetailState = _bookingDetailState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

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
                _state.value = BookingHistoryState.Error(e.message ?: "Unknown error")
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
                _bookingDetailState.value = BookingHistoryState.Error(e.message ?: "Unknown error")
            }
        }
    }
}