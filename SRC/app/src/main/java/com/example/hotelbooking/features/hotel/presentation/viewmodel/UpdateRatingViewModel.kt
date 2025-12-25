package com.example.hotelbooking.features.hotel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.usecase.update.UpdateHotelRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateRatingState {
    data object Idle : UpdateRatingState()
    data object Loading : UpdateRatingState()
    data object Success : UpdateRatingState()
    data class Error(val message: String) : UpdateRatingState()
}

@HiltViewModel
class UpdateRatingViewModel @Inject constructor(
    private val updateHotelRatingUseCase: UpdateHotelRatingUseCase
) : ViewModel() {

    private val _updateRatingState =
        MutableStateFlow<UpdateRatingState>(UpdateRatingState.Idle)
    val updateRatingState = _updateRatingState.asStateFlow()

    fun submitReview(
        hotelId: String,
        rating: Double
    ) {
        viewModelScope.launch {
            _updateRatingState.value = UpdateRatingState.Loading

            runCatching {
                updateHotelRatingUseCase(
                    hotelId = hotelId,
                    rating = rating
                )
            }.onSuccess {
                _updateRatingState.value = UpdateRatingState.Success
            }.onFailure { e ->
                _updateRatingState.value =
                    UpdateRatingState.Error(e.message ?: "Update rating failed")
            }
        }
    }
}