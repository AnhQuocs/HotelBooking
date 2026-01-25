package com.example.hotelbooking.features.transaction.domain.repository

import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Result<String>
    suspend fun getTransactionById(id: String): Result<Transaction?>
    suspend fun getTransactionByBookingId(bookingId: String): Result<Transaction?>
    suspend fun getPendingTransactionByBookingId(bookingId: String, userId: String): Result<Transaction?>
    suspend fun getTransactionsByUserId(userId: String): Result<List<Transaction>>
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        amountPaid: Double? = null
    ): Result<Unit>
}