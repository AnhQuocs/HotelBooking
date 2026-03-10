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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
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

    private var adminDetailJob: Job? = null

    fun loadBookingDetails(bookingId: String) {
        adminDetailJob?.cancel()

        adminDetailJob = viewModelScope.launch {
            getBookingDetailWithHotelUseCase(bookingId)
                .onStart {
                    _uiState.value = AdminBookingDetailState.Loading
                }
                .catch { e ->
                    _uiState.value = AdminBookingDetailState.Error(e.message ?: "Unknown Error")
                }
                .collect { combined ->
                    if (combined != null) {
                        _uiState.value = AdminBookingDetailState.Success(combined)
                    } else {
                        _uiState.value = AdminBookingDetailState.Error("Booking not found")
                    }
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