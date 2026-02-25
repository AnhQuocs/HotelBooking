package com.example.hotelbooking.features.transaction.data.mapper

import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.transaction.data.dto.TransactionDto
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        bookingId = bookingId,
        userId = userId,
        status = TransactionStatus.valueOf(status),
        totalPrice = totalPrice,
        amountPaid = amountPaid,
        paymentMethod = paymentMethod?.let {
            try { PaymentBrand.valueOf(it) } catch (e: Exception) { null }
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
        refundedAt = refundedAt
    )
}

fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = id,
        bookingId = bookingId,
        userId = userId,
        status = status.name,
        totalPrice = totalPrice,
        amountPaid = amountPaid,
        paymentMethod = paymentMethod?.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        refundedAt = refundedAt
    )
}