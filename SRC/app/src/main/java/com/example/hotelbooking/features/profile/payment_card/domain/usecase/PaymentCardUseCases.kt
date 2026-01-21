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
        validateCard(paymentCard)?.let { error ->
            return Result.failure(PaymentCardException(error))
        }

        return try {
            val existingCards = repository.getPaymentCards(paymentCard.userId)

            val shouldBeDefault = existingCards.isEmpty() || paymentCard.isDefault

            if (shouldBeDefault) {
                existingCards.filter { it.isDefault }.forEach { card ->
                    repository.updatePaymentCard(card.copy(isDefault = false))
                }
            }

            repository.createPaymentCard(paymentCard.copy(isDefault = shouldBeDefault))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateCard(card: PaymentCard): PaymentCardError? {
        if (!card.cardNumber.all { it.isDigit() } || card.cardNumber.length < 12) {
            return PaymentCardError.InvalidCardNumber
        }

        if (card.expiryMonth !in 1..12) {
            return PaymentCardError.InvalidExpiryDate
        }

        val now = java.time.LocalDate.now()
        if (card.expiryYear < now.year ||
            (card.expiryYear == now.year && card.expiryMonth < now.monthValue)) {
            return PaymentCardError.CardExpired
        }

        if (!card.cvv.all { it.isDigit() } || card.cvv.length !in 3..4) {
            return PaymentCardError.InvalidCvv
        }

        return null
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