package com.example.hotelbooking.features.transaction.data.repository

import com.example.hotelbooking.features.transaction.data.dto.TransactionDto
import com.example.hotelbooking.features.transaction.data.mapper.toDomain
import com.example.hotelbooking.features.transaction.data.mapper.toDto
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TransactionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TransactionRepository {

    private val transactionCollection = firestore.collection("transactions")

    override suspend fun createTransaction(transaction: Transaction): Result<String> = try {
        val docRef = transactionCollection.document()
        val dto = transaction.copy(id = docRef.id).toDto()
        docRef.set(dto).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTransactionById(id: String): Result<Transaction?> = try {
        val snapshot = transactionCollection.document(id).get().await()
        val transaction = snapshot.toObject(TransactionDto::class.java)?.toDomain()
        Result.success(transaction)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTransactionByBookingId(bookingId: String): Result<Transaction?> = try {
        val snapshot = transactionCollection
            .whereEqualTo("bookingId", bookingId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get().await()

        val transaction = snapshot.documents.firstOrNull()?.toObject(TransactionDto::class.java)?.toDomain()
        Result.success(transaction)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPendingTransactionByBookingId(
        bookingId: String,
        userId: String
    ): Result<Transaction?> = try {
        val snapshot = transactionCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("bookingId", bookingId)
            .whereEqualTo("status", TransactionStatus.PENDING.name)
            .limit(1)
            .get().await()

        val transaction = snapshot.documents.firstOrNull()
            ?.toObject(TransactionDto::class.java)?.toDomain()
        Result.success(transaction)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTransactionsByUserId(userId: String): Result<List<Transaction>> = try {
        val snapshots = transactionCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()

        val list = snapshots.documents.mapNotNull {
            it.toObject(TransactionDto::class.java)?.toDomain()
        }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }
}