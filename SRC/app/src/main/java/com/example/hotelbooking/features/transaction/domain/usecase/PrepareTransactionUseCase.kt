package com.example.hotelbooking.features.transaction.domain.usecase

import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.repository.TransactionRepository
import javax.inject.Inject

class PrepareTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(template: Transaction): Result<String> {
        val existingResult = repository.getPendingTransactionByBookingId(template.bookingId, template.userId)

        return existingResult.fold(
            onSuccess = { existingTx ->
                if (existingTx != null) {
                    Result.success(existingTx.id)
                } else {
                    repository.createTransaction(template)
                }
            },
            onFailure = { Result.failure(it) }
        )
    }
}