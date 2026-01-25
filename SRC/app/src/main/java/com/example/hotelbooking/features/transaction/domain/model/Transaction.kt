package com.example.hotelbooking.features.transaction.domain.model

import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand

enum class TransactionStatus {
    PAID, PENDING, CANCELLED, REFUND
}

data class Transaction(
    val id: String = "",
    val bookingId: String,
    val userId: String,
    val status: TransactionStatus,
    val totalPrice: Double,
    val amountPaid: Double,
    val paymentMethod: PaymentBrand? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val refundedAt: Long?
)