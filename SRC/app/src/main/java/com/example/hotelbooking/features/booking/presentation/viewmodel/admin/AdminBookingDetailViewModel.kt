package com.example.hotelbooking.features.booking.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingDetailWithHotelUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.CheckOutUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateStayStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminBookingDetailState {
    object Loading : AdminBookingDetailState()
    data class Success(val bookingWithHotel: BookingWithHotel) : AdminBookingDetailState()
    data class Error(val message: String) : AdminBookingDetailState()
}

@HiltViewModel
class AdminBookingDetailViewModel @Inject constructor(
    private val getBookingDetailWithHotelUseCase: GetBookingDetailWithHotelUseCase,
    private val updateStayStatusUseCase: UpdateStayStatusUseCase,
    private val checkOutUseCase: CheckOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminBookingDetailState>(AdminBookingDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    fun loadBookingDetails(bookingId: String) {
        viewModelScope.launch {
            _uiState.value = AdminBookingDetailState.Loading
            try {
                val combined = getBookingDetailWithHotelUseCase(bookingId)
                _uiState.value = AdminBookingDetailState.Success(combined)
            } catch (e: Exception) {
                _uiState.value = AdminBookingDetailState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun processCheckOut(booking: Booking) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val isSuccess = checkOutUseCase(booking)
                if (isSuccess) {
                    loadBookingDetails(booking.bookingId)
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun markAsNoShow(bookingId: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                updateStayStatusUseCase(bookingId, StayStatus.NO_SHOW)
                loadBookingDetails(bookingId)
            } finally {
                _isProcessing.value = false
            }
        }
    }
}