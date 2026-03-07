package com.example.hotelbooking.features.vouchers.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.vouchers.data.dto.VoucherDto
import com.example.hotelbooking.features.vouchers.data.mapper.VoucherMapper
import com.example.hotelbooking.features.vouchers.domain.model.AdminVoucher
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.domain.usecase.AdminVoucherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class AdminVoucherState {
    object Loading : AdminVoucherState()
    data class Success(val vouchers: List<AdminVoucher>) : AdminVoucherState()
    data class Error(val message: String) : AdminVoucherState()
}

data class AddVoucherUiState(
    val code: String = "",
    val titleVi: String = "",
    val titleEn: String = "",
    val discountValue: String = "",
    val minOrderValue: String = "",
    val totalQuantity: String = "",
    val endDate: Long = System.currentTimeMillis() + 604800000,
    val selectedHotelIds: Set<String> = emptySet(),
    val discountType: DiscountType = DiscountType.FIXED_AMOUNT,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminVoucherViewModel @Inject constructor(
    private val adminUseCases: AdminVoucherUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminVoucherState>(AdminVoucherState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadVouchers(adminId: String) {
        viewModelScope.launch {
            _uiState.value = AdminVoucherState.Loading
            adminUseCases.getAdminVouchersUseCase(adminId)
                .catch { e ->
                    _uiState.value = AdminVoucherState.Error(e.message ?: "Unknown Error")
                }
                .collect { list ->
                    _uiState.value = AdminVoucherState.Success(list)
                }
        }
    }

    private val _addVoucherState = MutableStateFlow(AddVoucherUiState())
    val addVoucherState = _addVoucherState.asStateFlow()

    fun onCodeChange(newValue: String) { _addVoucherState.update { it.copy(code = newValue) } }
    fun onTitleViChange(newValue: String) { _addVoucherState.update { it.copy(titleVi = newValue) } }
    fun onTitleEnChange(newValue: String) { _addVoucherState.update { it.copy(titleEn = newValue) } }
    fun onDiscountValueChange(newValue: String) { _addVoucherState.update { it.copy(discountValue = newValue) } }
    fun onMinOrderValueChange(newValue: String) { _addVoucherState.update { it.copy(minOrderValue = newValue) } }
    fun onTotalQuantityChange(newValue: String) { _addVoucherState.update { it.copy(totalQuantity = newValue) } }
    fun onEndDateChange(newValue: Long) { _addVoucherState.update { it.copy(endDate = newValue) } }
    fun onDiscountTypeChange(newValue: DiscountType) { _addVoucherState.update { it.copy(discountType = newValue) } }

    fun toggleHotelSelection(hotelId: String) {
        _addVoucherState.update { state ->
            val newSelection = if (state.selectedHotelIds.contains(hotelId)) {
                state.selectedHotelIds - hotelId
            } else {
                state.selectedHotelIds + hotelId
            }
            state.copy(selectedHotelIds = newSelection)
        }
    }

    fun submitVouchers(adminId: String, onComplete: (Boolean) -> Unit) {
        val state = _addVoucherState.value
        if (state.selectedHotelIds.isEmpty() || state.code.isBlank()) {
            _addVoucherState.update { it.copy(errorMessage = "Please enter all the required information and select your hotel.") }
            return
        }

        viewModelScope.launch {
            _addVoucherState.update { it.updateLoading(true) }
            var allSuccessful = true

            state.selectedHotelIds.forEach { hotelId ->
                val dto = VoucherMapper.fromUiStateToDto(adminId, hotelId, state)

                val result = adminUseCases.createVoucherUseCase(dto)
                if (result.isFailure) allSuccessful = false
            }

            if (allSuccessful) {
                resetAddVoucherState()
                onComplete(true)
            } else {
                _addVoucherState.update { it.copy(isLoading = false, errorMessage = "There was an error when creating some vouchers.") }
                onComplete(false)
            }
        }
    }

    private fun resetAddVoucherState() {
        _addVoucherState.value = AddVoucherUiState()
    }

    fun toggleVoucher(voucherId: String, isActive: Boolean) {
        viewModelScope.launch {
            adminUseCases.toggleVoucherActiveUseCase(voucherId, isActive)
        }
    }
}

fun AddVoucherUiState.updateLoading(loading: Boolean) = this.copy(isLoading = loading, errorMessage = null)