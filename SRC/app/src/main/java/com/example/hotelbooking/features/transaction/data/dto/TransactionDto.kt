package com.example.hotelbooking.features.transaction.data.dto

data class TransactionDto(
    val id: String = "",
    val bookingId: String= "",
    val userId: String= "",
    val status: String = "PENDING",
    val totalPrice: Double = 0.0,
    val amountPaid: Double = 0.0,
    val paymentMethod: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val refundedAt: Long? = null
)