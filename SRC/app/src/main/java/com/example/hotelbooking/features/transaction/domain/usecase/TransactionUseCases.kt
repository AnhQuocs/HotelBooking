package com.example.hotelbooking.features.transaction.domain.usecase

import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.domain.repository.TransactionRepository
import javax.inject.Inject

data class TransactionUseCases @Inject constructor(
    val createTransactionUseCase: CreateTransactionUseCase,
    val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    val getTransactionByBookingIdUseCase: GetTransactionByBookingIdUseCase,
    val getUserTransactionsUseCase: GetUserTransactionsUseCase
)

class CreateTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) =
        repository.createTransaction(transaction)
}

class GetTransactionByIdUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(id: String) : Result<Transaction?> {
        return repository.getTransactionById(id)
    }
}

class GetTransactionByBookingIdUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(bookingId: String): Result<Transaction?> {
        return repository.getTransactionByBookingId(bookingId)
    }
}

class GetUserTransactionsUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(userId: String) =
        repository.getTransactionsByUserId(userId)
}