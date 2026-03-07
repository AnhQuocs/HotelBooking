package com.example.hotelbooking.features.vouchers.presentation.util

import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.AddVoucherUiState

object VoucherValidationUtil {
    fun AddVoucherUiState.canSubmit(): Boolean {
        val valDouble = discountValue.toDoubleOrNull() ?: 0.0
        val isDiscountValid = if (discountType == DiscountType.PERCENTAGE) {
            valDouble in 1.0..100.0
        } else {
            valDouble > 0.0
        }

        val isDateValid = endDate > System.currentTimeMillis()

        return code.isNotBlank() &&
                titleVi.isNotBlank() &&
                titleEn.isNotBlank() &&
                isDiscountValid &&
                (totalQuantity.toIntOrNull() ?: 0) > 0 &&
                selectedHotelIds.isNotEmpty() &&
                isDateValid &&
                !isLoading
    }
}