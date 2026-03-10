package com.example.hotelbooking.features.profile.feature.payment_card.domain.repository

import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard
import kotlinx.coroutines.flow.Flow


interface PaymentCardRepository {
    suspend fun createPaymentCard(paymentCard: PaymentCard)

    suspend fun updatePaymentCard(paymentCard: PaymentCard)

    fun getPaymentCards(userId: String): Flow<List<PaymentCard>>

    fun getPaymentCardById(id: String): Flow<PaymentCard?>

    suspend fun deletePaymentCard(id: String)
}