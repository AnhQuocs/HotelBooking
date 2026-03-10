package com.example.hotelbooking.features.booking.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.Guest
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingByIdUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateBookingUseCase
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StayViewModel @Inject constructor(
    private val updateBookingUseCase: UpdateBookingUseCase,
    private val getBookingByIdUseCase: GetBookingByIdUseCase
) : ViewModel() {
    private val _guestListState = MutableStateFlow<List<Guest>>(emptyList())
    val guestListState = _guestListState.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    private val _bookingState = MutableStateFlow<Booking?>(null)
    val bookingState = _bookingState.asStateFlow()

    private var bookingDetailJob: Job? = null

    fun loadBookingData(bookingId: String) {
        bookingDetailJob?.cancel()

        bookingDetailJob = viewModelScope.launch {
            getBookingByIdUseCase(bookingId)
                .collect { booking ->
                    if (booking != null) {
                        _bookingState.value = booking

                        initGuestList(booking)
                    }
                }
        }
    }

    private fun initGuestList(booking: Booking) {
        val currentGuests = booking.guests.toMutableList()
        val totalNeeded = booking.numberOfGuests

        if (currentGuests.size < totalNeeded) {
            val needed = totalNeeded - currentGuests.size
            repeat(needed) {
                currentGuests.add(
                    Guest(id = UUID.randomUUID().toString(), fullName = "", isRepresentative = false)
                )
            }
        }
        _guestListState.value = currentGuests.sortedByDescending { it.isRepresentative }
    }

    fun updateGuestInfo(index: Int, updatedGuest: Guest) {
        val currentList = _guestListState.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = updatedGuest
            _guestListState.value = currentList
        }
    }

    fun submitCheckIn(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val originalBooking = _bookingState.value

        if (originalBooking == null) {
            onError("Dữ liệu chưa tải xong. Vui lòng đợi...")
            return
        }

        val finalGuestList = _guestListState.value

        if (finalGuestList.any { it.fullName.isBlank() }) {
            onError("Vui lòng nhập đầy đủ tên khách hàng")
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true

            val updatedBooking = originalBooking.copy(
                guests = finalGuestList,
                stayStatus = StayStatus.CHECK_IN,
                updatedAt = Timestamp.now()
            )

            val result = updateBookingUseCase(updatedBooking)

            if (result) {
                onSuccess()
            } else {
                onError("Lỗi hệ thống. Không thể Check-in.")
            }
            _isSubmitting.value = false
        }
    }
}