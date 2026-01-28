package com.example.hotelbooking.features.booking.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.booking.domain.usecase.update.RebookBookingTransactionUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateBookingUseCase
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class UpdateBookingState {
    object Idle : UpdateBookingState()
    object Loading : UpdateBookingState()
    data class Success(val message: String) : UpdateBookingState()
    data class Error(val message: String) : UpdateBookingState()
}

@HiltViewModel
class UpdateBookingViewModel @Inject constructor(
    private val updateBookingUseCase: UpdateBookingUseCase,
    private val bookingRepository: BookingRepository,
    private val rebookBookingTransactionUseCase: RebookBookingTransactionUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    var selectedBooking by mutableStateOf<Booking?>(null)

    private val _updateState = MutableStateFlow<UpdateBookingState>(UpdateBookingState.Idle)
    val updateState = _updateState.asStateFlow()

    fun updateGuestInfo(
        newName: String,
        newEmail: String,
        newAge: Int,
        newPhone: String,
        newNumberOfGuest: Int
    ) {
        selectedBooking?.let { current ->
            val updatedBooking = current.copy(
                guest = current.guest.copy(
                    name = newName,
                    email = newEmail,
                    phone = newPhone,
                    age = newAge
                ),
                numberOfGuests = newNumberOfGuest
            )
            executeRequest(updatedBooking, context.getString(R.string.update_guest_success))
        }
    }

    fun confirmRebook(
        currentBooking: Booking,
        newCheckIn: Long,
        newCheckOut: Long,
        newTotalPrice: Double
    ) {
        val userId = FirebaseAuth.getInstance().uid ?: ""
        val now = System.currentTimeMillis()

        val updatedBooking = currentBooking.copy(
            startDate = newCheckIn.toTimestamp(),
            endDate = newCheckOut.toTimestamp(),
            totalPrice = newTotalPrice,
            status = BookingStatus.CONFIRMED,
            stayStatus = StayStatus.NONE,
            cancelReason = null,
            updatedAt = Timestamp.now()
        )

        val newTransaction = Transaction(
            bookingId = currentBooking.bookingId,
            userId = userId,
            status = TransactionStatus.PAID,
            totalPrice = newTotalPrice,
            amountPaid = 0.0,
            createdAt = now,
            updatedAt = now,
            refundedAt = null
        )

        executeRebookRequest(updatedBooking, newTransaction, context.getString(R.string.rebook_success))
    }

    private fun executeRequest(booking: Booking, successMessage: String) {
        val userId = FirebaseAuth.getInstance().uid ?: ""

        viewModelScope.launch {
            _updateState.value = UpdateBookingState.Loading
            try {
                val result = updateBookingUseCase(booking)
                if (result) {
                    _updateState.value = UpdateBookingState.Success(successMessage)
                    bookingRepository.clearCache(userId)
                } else {
                    _updateState.value = UpdateBookingState.Error(context.getString(R.string.update_booking_failed))
                }
            } catch (e: Exception) {
                _updateState.value = UpdateBookingState.Error(context.getString(R.string.system_error))
            }
        }
    }

    private fun executeRebookRequest(
        booking: Booking,
        transaction: Transaction,
        successMessage: String
    ) {
        val userId = FirebaseAuth.getInstance().uid ?: ""

        viewModelScope.launch {
            _updateState.value = UpdateBookingState.Loading
            try {
                val result = rebookBookingTransactionUseCase(booking.bookingId, booking, transaction)

                if (result.isSuccess) {
                    _updateState.value = UpdateBookingState.Success(successMessage)
                    bookingRepository.clearCache(userId)
                } else {
                    _updateState.value = UpdateBookingState.Error(context.getString(R.string.update_booking_failed))
                }
            } catch (e: Exception) {
                _updateState.value = UpdateBookingState.Error(context.getString(R.string.system_error))
            }
        }
    }

    fun resetState() {
        _updateState.value = UpdateBookingState.Idle
    }

    fun Long.toTimestamp(): Timestamp {
        return Timestamp(Date(this))
    }
}