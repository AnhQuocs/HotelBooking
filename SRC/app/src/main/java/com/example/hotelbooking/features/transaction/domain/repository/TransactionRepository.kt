package com.example.hotelbooking.features.transaction.domain.repository

import com.example.hotelbooking.features.transaction.domain.model.Transaction

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Result<String>
    suspend fun getTransactionById(id: String): Result<Transaction?>
    suspend fun getTransactionByBookingId(bookingId: String): Result<Transaction?>
    suspend fun getPendingTransactionByBookingId(bookingId: String, userId: String): Result<Transaction?>
    suspend fun getTransactionsByUserId(userId: String): Result<List<Transaction>>
}