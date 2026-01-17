package com.example.hotelbooking.features.profile.payment_card.domain.usecase

import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.payment_card.domain.repository.PaymentCardRepository
import javax.inject.Inject

data class PaymentCardUseCases @Inject constructor(
    val createPaymentCardUseCase: CreatePaymentCardUseCase,
    val updatePaymentCardUseCase: UpdatePaymentCardUseCase,
    val getPaymentCards: GetPaymentCardsUseCase,
    val getPaymentCardById: GetPaymentCardByIdUseCase,
    val deletePaymentCard: DeletePaymentCardUseCase
)

sealed class PaymentCardError(val messageKey: String) {
    object InvalidCardNumber : PaymentCardError("payment.card.invalid_number")
    object InvalidExpiryDate : PaymentCardError("payment.card.invalid_expiry")
    object InvalidCvv : PaymentCardError("payment.card.invalid_cvv")
    object CardExpired : PaymentCardError("payment.card.expired")
}

class PaymentCardException(
    val error: PaymentCardError
) : Exception(error.messageKey)

class CreatePaymentCardUseCase @Inject constructor(
    private val repository: PaymentCardRepository
) {

    suspend operator fun invoke(paymentCard: PaymentCard): Result<Unit> {

        if (!paymentCard.cardNumber.all { it.isDigit() } ||
            paymentCard.cardNumber.length < 12
        ) {
            return Result.failure(
                PaymentCardException(
                    PaymentCardError.InvalidCardNumber
                )
            )
        }

        if (paymentCard.expiryMonth !in 1..12) {
            return Result.failure(
                PaymentCardException(
                    PaymentCardError.InvalidExpiryDate
                )
            )
        }

        val now = java.time.LocalDate.now()

        if (
            paymentCard.expiryYear < now.year ||
            (paymentCard.expiryYear == now.year &&
                    paymentCard.expiryMonth < now.monthValue)
        ) {
            return Result.failure(
                PaymentCardException(
                    PaymentCardError.CardExpired
                )
            )
        }

        if (!paymentCard.cvv.all { it.isDigit() } ||
            paymentCard.cvv.length !in 3..4
        ) {
            return Result.failure(
                PaymentCardException(
                    PaymentCardError.InvalidCvv
                )
            )
        }

        if (paymentCard.isDefault) {
            val cards =
                repository.getPaymentCards(paymentCard.userId)

            cards
                .filter { it.isDefault }
                .forEach {
                    repository.updatePaymentCard(
                        it.copy(isDefault = false)
                    )
                }
        }

        repository.createPaymentCard(paymentCard)
        return Result.success(Unit)
    }
}

class UpdatePaymentCardUseCase @Inject constructor(
    private val repository: PaymentCardRepository
) {
    suspend operator fun invoke(paymentCard: PaymentCard) {

        if (paymentCard.isDefault) {
            val cards = repository.getPaymentCards(paymentCard.userId)
            cards
                .filter { it.id != paymentCard.id && it.isDefault }
                .forEach {
                    repository.updatePaymentCard(
                        it.copy(isDefault = false)
                    )
                }
        }

        repository.updatePaymentCard(paymentCard)
    }
}

class GetPaymentCardsUseCase @Inject constructor(
    private val repository: PaymentCardRepository
) {
    suspend operator fun invoke(userId: String): List<PaymentCard> {
        return repository.getPaymentCards(userId)
    }
}

class GetPaymentCardByIdUseCase @Inject constructor(
    private val repository: PaymentCardRepository
) {
    suspend operator fun invoke(id: String): PaymentCard? {
        return repository.getPaymentCardById(id)
    }
}

class DeletePaymentCardUseCase @Inject constructor(
    private val repository: PaymentCardRepository
) {
    suspend operator fun invoke(id: String) {
        return repository.deletePaymentCard(id)
    }
}