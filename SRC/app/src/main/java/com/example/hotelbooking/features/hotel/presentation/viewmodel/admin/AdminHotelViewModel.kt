package com.example.hotelbooking.features.hotel.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.domain.usecase.AdminHotelUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
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

    private val _updateStatusResult = MutableStateFlow<AdminHotelState<Unit>?>(null)
    val updateStatusResult = _updateStatusResult.asStateFlow()

    fun observeHotels(adminId: String) {
        viewModelScope.launch {
            adminHotelUseCases.getHotelsByAdminIdUseCase(adminId)
                .onStart {
                    _adminHotelState.value = AdminHotelState.Loading
                }
                .catch { e ->
                    _adminHotelState.value =
                        AdminHotelState.Error(e.message ?: "Unknown error")
                }
                .collect { hotelList ->
                    _adminHotelState.value =
                        AdminHotelState.Success(hotelList)
                }
        }
    }

    fun updateHotelStatus(hotelId: String, status: HotelStatus) {
        viewModelScope.launch {
            _updateStatusResult.value = AdminHotelState.Loading
            try {
                adminHotelUseCases.updateHotelStatusUseCase(hotelId, status)
                _updateStatusResult.value = AdminHotelState.Success(Unit)

                _updateStatusResult.value = null
            } catch (e: Exception) {
                _updateStatusResult.value = AdminHotelState.Error(e.message ?: "Update fail")
            }
        }
    }
}