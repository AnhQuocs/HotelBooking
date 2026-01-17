package com.example.hotelbooking.features.profile.payment_card.data.repository

import com.example.hotelbooking.features.profile.payment_card.data.dto.PaymentCardDto
import com.example.hotelbooking.features.profile.payment_card.data.mapper.toDomain
import com.example.hotelbooking.features.profile.payment_card.data.mapper.toDto
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.payment_card.domain.repository.PaymentCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PaymentCardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PaymentCardRepository {

    private val collection = firestore.collection("payment_cards")

    override suspend fun createPaymentCard(paymentCard: PaymentCard) {
        collection
            .document(paymentCard.id)
            .set(paymentCard.toDto(paymentCard.userId))
            .await()
    }

    override suspend fun updatePaymentCard(paymentCard: PaymentCard) {
        collection
            .document(paymentCard.id)
            .set(paymentCard.toDto(paymentCard.userId))
            .await()
    }

    override suspend fun getPaymentCards(userId: String): List<PaymentCard> {
        return collection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(PaymentCardDto::class.java) }
            .map { it.toDomain() }
    }

    override suspend fun getPaymentCardById(id: String): PaymentCard? {
        return collection
            .document(id)
            .get()
            .await()
            .toObject(PaymentCardDto::class.java)
            ?.toDomain()
    }

    override suspend fun deletePaymentCard(id: String) {
        collection
            .document(id)
            .delete()
            .await()
    }
}