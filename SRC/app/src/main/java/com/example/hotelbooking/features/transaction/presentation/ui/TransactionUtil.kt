package com.example.hotelbooking.features.transaction.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.hotelbooking.R
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.IndigoBlue
import com.example.hotelbooking.ui.theme.RatingYellow

@Composable
fun getStatusColor(status: TransactionStatus): Color = when (status) {
    TransactionStatus.PAID -> AvailableGreen
    TransactionStatus.PENDING -> RatingYellow
    TransactionStatus.CANCELLED -> CancelledRed
    TransactionStatus.REFUND -> IndigoBlue
}

@Composable
fun getPaymentBrandIcon(brand: PaymentBrand): Int = when (brand) {
    PaymentBrand.VISA -> R.drawable.ic_visa
    PaymentBrand.MASTERCARD -> R.drawable.ic_mastercard
    PaymentBrand.JCB -> R.drawable.ic_jcb
}