package com.example.hotelbooking.features.booking.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.usecase.BookingUseCases
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.usecase.HotelUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingWithHotel(
    val booking: Booking,
    val hotel: Hotel?
)

sealed class BookingHistoryState<out T> {
    object Loading : BookingHistoryState<Nothing>()
    data class Success<T>(val data: T) : BookingHistoryState<T>()
    data class Error(val message: String) : BookingHistoryState<Nothing>()
}

@HiltViewModel
class BookingHistoryViewModel @Inject constructor(
    private val bookingUseCases: BookingUseCases,
    private val hotelUseCase: HotelUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<BookingHistoryState<List<BookingWithHotel>>>(BookingHistoryState.Loading)
    val state = _state.asStateFlow()

    private val _bookingDetailState = MutableStateFlow<BookingHistoryState<BookingWithHotel>>(BookingHistoryState.Loading)
    val bookingDetailState = _bookingDetailState.asStateFlow()

    fun loadMyBookings(userId: String) {
        viewModelScope.launch {
            Log.d("BookingHistoryVM", "🚀 loadMyBookings START - userId=$userId")

            _state.value = BookingHistoryState.Loading
            Log.d("BookingHistoryVM", "🔄 State = Loading")

            try {
                Log.d("BookingHistoryVM", "📡 Fetch bookings by user...")
                val bookings = bookingUseCases.getBookingsByUserUseCase(userId)

                Log.d(
                    "BookingHistoryVM",
                    "✅ Bookings fetched: size=${bookings.size}, ids=${bookings.map { it.bookingId }}"
                )

                val combinedList = bookings.map { booking ->
                    async {
                        Log.d(
                            "BookingHistoryVM",
                            "🏨 Fetch hotel for bookingId=${booking.bookingId}, hotelId=${booking.hotelId}"
                        )

                        val hotel = hotelUseCase.getHotelByIdUseCase(booking.hotelId)

                        Log.d(
                            "BookingHistoryVM",
                            "✅ Hotel fetched: bookingId=${booking.bookingId}, hotelName=${hotel?.name}"
                        )

                        BookingWithHotel(booking, hotel)
                    }
                }.awaitAll()

                Log.d("BookingHistoryVM", "🎉 Combined list ready: size=${combinedList.size}")

                _state.value = BookingHistoryState.Success(combinedList)
                Log.d("BookingHistoryVM", "✅ State = Success")

            } catch (e: Exception) {
                Log.e("BookingHistoryVM", "❌ ERROR loadMyBookings", e)
                _state.value = BookingHistoryState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadBookingById(bookingId: String) {
        viewModelScope.launch {
            _bookingDetailState.value = BookingHistoryState.Loading
            try {
                val booking = bookingUseCases.getBookingByIdUseCase(bookingId)

                val hotel = hotelUseCase.getHotelByIdUseCase(booking.hotelId)
                val combined = BookingWithHotel(booking, hotel)

                _bookingDetailState.value = BookingHistoryState.Success(combined)
            } catch (e: Exception) {
                _state.value = BookingHistoryState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun cancelBooking(bookingId: String, userId: String) {
        viewModelScope.launch {
            _state.value = BookingHistoryState.Loading
            try {
                val success = bookingUseCases.cancelBookingUseCase(bookingId)
                if (success) {
                    loadMyBookings(userId)
                } else {
                    _state.value = BookingHistoryState.Error("Failed to cancel booking")
                }
            } catch (e: Exception) {
                _state.value = BookingHistoryState.Error(e.message ?: "Unknown error")
            }
        }
    }
}