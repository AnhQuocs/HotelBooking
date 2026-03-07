package com.example.hotelbooking.features.vouchers.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.domain.usecase.UserVoucherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserVoucherState {
    object Loading : UserVoucherState()
    data class Success(val vouchers: List<Voucher>) : UserVoucherState()
    data class Error(val message: String) : UserVoucherState()
}

@HiltViewModel
class UserVoucherViewModel @Inject constructor(
    private val userUseCases: UserVoucherUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserVoucherState>(UserVoucherState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _groupedVouchers = MutableStateFlow<Map<Hotel, List<Voucher>>>(emptyMap())
    val groupedVouchers = _groupedVouchers.asStateFlow()

    fun groupVouchersByHotel(vouchers: List<Voucher>, allHotels: List<Hotel>) {
        val hotelIdsWithVouchers = vouchers.map { it.hotelId }.toSet()

        val activeHotels = allHotels.filter { it.id in hotelIdsWithVouchers }

        val grouped = activeHotels.associateWith { hotel ->
            vouchers.filter { it.hotelId == hotel.id }
        }

        _groupedVouchers.value = grouped
    }

    fun loadUserVouchers(userId: String) {
        viewModelScope.launch {
            _uiState.value = UserVoucherState.Loading
            userUseCases.getVouchersWithStatusUseCase(userId)
                .catch { e ->
                    _uiState.value = UserVoucherState.Error(e.message ?: "Unknown Error")
                }
                .collect { vouchers ->
                    _uiState.value = UserVoucherState.Success(vouchers)
                }
        }
    }

    fun applyVoucher(userId: String, voucherId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = userUseCases.applyVoucherUseCase(userId, voucherId)
            onResult(result)
        }
    }
}