package com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.payment_card.domain.usecase.PaymentCardException
import com.example.hotelbooking.features.profile.payment_card.domain.usecase.PaymentCardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentCardState<out T> {
    data object Idle : PaymentCardState<Nothing>()
    data object Loading : PaymentCardState<Nothing>()
    data class Success<T>(val data: T) : PaymentCardState<T>()
    data class Error(val messageKey: String) : PaymentCardState<Nothing>()
}

@HiltViewModel
class PaymentCardViewModel @Inject constructor(
    private val paymentCardUseCases: PaymentCardUseCases
) : ViewModel() {

    private val _cardsState =
        MutableStateFlow<PaymentCardState<List<PaymentCard>>>(
            PaymentCardState.Loading
        )
    val cardsState: StateFlow<PaymentCardState<List<PaymentCard>>> =
        _cardsState

    private val _cardState = MutableStateFlow<PaymentCardState<PaymentCard?>>(
        PaymentCardState.Idle
    )
    val cardState: StateFlow<PaymentCardState<PaymentCard?>> =
        _cardState


    fun createPaymentCard(card: PaymentCard) {
        viewModelScope.launch {
            _cardState.value = PaymentCardState.Loading

            val result =
                paymentCardUseCases.createPaymentCardUseCase(card)

            _cardState.value = result.fold(
                onSuccess = {
                    PaymentCardState.Success(null)
                },
                onFailure = { throwable ->
                    val messageKey =
                        (throwable as? PaymentCardException)
                            ?.error
                            ?.messageKey
                            ?: "common.error"

                    PaymentCardState.Error(messageKey)
                }
            )
        }
    }

    fun updatePaymentCard(card: PaymentCard) {
        viewModelScope.launch {
            _cardState.value = PaymentCardState.Loading
            try {
                paymentCardUseCases.updatePaymentCardUseCase(card)
                _cardState.value = PaymentCardState.Success(null)
            } catch (e: Exception) {
                val messageKey = (e as? PaymentCardException)?.error?.messageKey ?: "common.error"
                _cardState.value = PaymentCardState.Error(messageKey)
            }
        }
    }

    fun loadPaymentCards(userId: String) {
        viewModelScope.launch {
            _cardsState.value = PaymentCardState.Loading
            try {
                val cards =
                    paymentCardUseCases.getPaymentCards(userId)
                _cardsState.value =
                    PaymentCardState.Success(cards)
            } catch (e: Exception) {
                _cardsState.value =
                    PaymentCardState.Error("common.error")
            }
        }
    }

    fun loadPaymentCardById(id: String) {
        viewModelScope.launch {
            _cardState.value = PaymentCardState.Loading
            try {
                val card =
                    paymentCardUseCases.getPaymentCardById(id)
                        ?: throw IllegalStateException()
                _cardState.value =
                    PaymentCardState.Success(card)
            } catch (e: Exception) {
                _cardState.value =
                    PaymentCardState.Error("common.error")
            }
        }
    }

    fun deletePaymentCard(id: String) {
        viewModelScope.launch {
            _cardState.value = PaymentCardState.Loading
            try {
                paymentCardUseCases.deletePaymentCard(id)
                _cardState.value =
                    PaymentCardState.Success(null)
            } catch (e: Exception) {
                _cardState.value =
                    PaymentCardState.Error("common.error")
            }
        }
    }

    fun resetCardState() {
        _cardState.value = PaymentCardState.Idle
    }
}