package com.example.hotelbooking.features.profile.feature.payment_card.data.repository

import com.example.hotelbooking.features.profile.feature.payment_card.data.dto.PaymentCardDto
import com.example.hotelbooking.features.profile.feature.payment_card.data.mapper.toDomain
import com.example.hotelbooking.features.profile.feature.payment_card.data.mapper.toDto
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.feature.payment_card.domain.repository.PaymentCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

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

    override fun getPaymentCards(userId: String): Flow<List<PaymentCard>> {
        return collection
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(PaymentCardDto::class.java)?.toDomain()
                }
            }
    }

    override fun getPaymentCardById(id: String): Flow<PaymentCard?> {
        return collection
            .document(id)
            .snapshots()
            .map { documentSnapshot ->
                documentSnapshot.toObject(PaymentCardDto::class.java)?.toDomain()
            }
    }

    override suspend fun deletePaymentCard(id: String) {
        collection
            .document(id)
            .delete()
            .await()
    }
}