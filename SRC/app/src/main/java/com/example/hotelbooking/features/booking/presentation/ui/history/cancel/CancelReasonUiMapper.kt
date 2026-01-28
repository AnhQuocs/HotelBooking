package com.example.hotelbooking.features.booking.presentation.ui.history.cancel

import androidx.annotation.StringRes
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.CancelReason

@StringRes
fun CancelReason.toUserLabelRes(): Int = when (this) {
    CancelReason.CHANGE_PLAN -> R.string.cancel_reason_change_plan
    CancelReason.WRONG_INFO -> R.string.cancel_reason_wrong_info
    CancelReason.PAYMENT_ISSUE -> R.string.cancel_reason_payment_issue
    CancelReason.FIND_BETTER_PRICE -> R.string.cancel_reason_find_better_price
    CancelReason.SYSTEM_ERROR -> R.string.cancel_reason_system_error
    CancelReason.OTHER -> R.string.cancel_reason_other
    CancelReason.TIMEOUT ->
        error("TIMEOUT must not be shown to user")
}