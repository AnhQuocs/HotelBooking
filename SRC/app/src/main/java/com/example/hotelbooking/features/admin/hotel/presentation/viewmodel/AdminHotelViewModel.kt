package com.example.hotelbooking.features.admin.hotel.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.usecase.AdminHotelUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminHotelState<out T> {
    data object Loading : AdminHotelState<Nothing>()
    data class Success<T>(val data: T) : AdminHotelState<T>()
    data class Error(val message: String) : AdminHotelState<Nothing>()
}

@HiltViewModel
class AdminHotelViewModel @Inject constructor(
    private val adminHotelUseCases: AdminHotelUseCases
) : ViewModel() {

    private val _adminHotelState = MutableStateFlow<AdminHotelState<List<Hotel>>>(AdminHotelState.Loading)
    val adminHotelState = _adminHotelState.asStateFlow()

    fun loadAdminHotels(adminId: String) {
        viewModelScope.launch {
            _adminHotelState.value = AdminHotelState.Loading

            runCatching {
                adminHotelUseCases.getHotelsByAdminIdUseCase(adminId)
            }.onSuccess { hotels ->
                _adminHotelState.value = AdminHotelState.Success(hotels)
                Log.d("LoadHotelsDebug", "ViewModel: $hotels")
            }.onFailure { e ->
                _adminHotelState.value = AdminHotelState.Error(e.message ?: "Unknown error")
            }
        }
    }
}