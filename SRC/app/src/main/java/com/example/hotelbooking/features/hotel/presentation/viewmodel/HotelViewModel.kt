package com.example.hotelbooking.features.hotel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.usecase.HotelUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HotelState<out T> {
    data object Loading : HotelState<Nothing>()
    data class Success<T>(val data: T) : HotelState<T>()
    data class Error(val message: String) : HotelState<Nothing>()
}

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelUseCases: HotelUseCases
) : ViewModel() {
    private val _hotelsState = MutableStateFlow<HotelState<List<Hotel>>>(HotelState.Loading)
    val hotelsState = _hotelsState.asStateFlow()

    private val _recommendedState = MutableStateFlow<HotelState<List<Hotel>>>(HotelState.Loading)
    val recommendedState = _recommendedState.asStateFlow()

    private val _hotelDetailState = MutableStateFlow<HotelState<Hotel>>(HotelState.Loading)
    val hotelDetailState = _hotelDetailState.asStateFlow()

    private val hotelCache = mutableMapOf<String, Hotel>()

    fun loadHotels() {
        viewModelScope.launch {
            _hotelsState.value = HotelState.Loading

            runCatching {
                hotelUseCases.getAllHotelsUseCase()
            }.onSuccess { hotels ->
                _hotelsState.value = HotelState.Success(hotels)
            }.onFailure { e ->
                _hotelsState.value = HotelState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadRecommendedHotels(minAverageRating: Double) {
        viewModelScope.launch {
            _recommendedState.value = HotelState.Loading

            runCatching {
                hotelUseCases.getRecommendedHotelsUseCase(minAverageRating)
            }.onSuccess { data ->
                _recommendedState.value = HotelState.Success(data)
            }.onFailure { e ->
                _recommendedState.value = HotelState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadHotelById(hotelId: String) {
        viewModelScope.launch {

            hotelCache[hotelId]?.let { cachedHotel ->
                _hotelDetailState.value = HotelState.Success(cachedHotel)
                return@launch
            }

            _hotelDetailState.value = HotelState.Loading

            runCatching {
                hotelUseCases.getHotelByIdUseCase(hotelId)
            }.onSuccess { hotel ->
                if (hotel != null) {
                    hotelCache[hotelId] = hotel
                    _hotelDetailState.value = HotelState.Success(hotel)
                } else {
                    _hotelDetailState.value = HotelState.Error("Hotel not found")
                }
            }.onFailure { e ->
                _hotelDetailState.value = HotelState.Error(e.message ?: "Unknown error")
            }
        }
    }
}