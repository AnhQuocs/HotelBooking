package com.example.hotelbooking.features.profile.payment_card.domain.repository

import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard

interface PaymentCardRepository {
    suspend fun createPaymentCard(paymentCard: PaymentCard)

    suspend fun updatePaymentCard(paymentCard: PaymentCard)

    suspend fun getPaymentCards(userId: String): List<PaymentCard>

    suspend fun getPaymentCardById(id: String): PaymentCard?

    suspend fun deletePaymentCard(id: String)
}