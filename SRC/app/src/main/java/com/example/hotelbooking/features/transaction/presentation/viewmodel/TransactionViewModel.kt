package com.example.hotelbooking.features.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentTimerManager
import com.example.hotelbooking.features.notification.domain.usecase.NotificationUseCases
import com.example.hotelbooking.features.notification.util.NotificationHelper
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.usecase.CompleteBookingPaymentUseCase
import com.example.hotelbooking.features.transaction.domain.usecase.PrepareTransactionUseCase
import com.example.hotelbooking.features.transaction.domain.usecase.TransactionUseCases
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TransactionAction {
    INITIALIZE,
    CONFIRM,
    UPDATE
}

sealed class TransactionState<out T> {
    object Idle : TransactionState<Nothing>()
    object Loading : TransactionState<Nothing>()
    data class Success<T>(val data: T) : TransactionState<T>()
    data class Error(val message: String) : TransactionState<Nothing>()
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases,
    private val completeBookingPaymentUseCase: CompleteBookingPaymentUseCase,
    private val prepareTransactionUseCase: PrepareTransactionUseCase,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper,
    private val timerManager: PaymentTimerManager,
) : ViewModel() {

    private val _historyState = MutableStateFlow<TransactionState<List<Transaction>>>(TransactionState.Idle)
    val historyState = _historyState.asStateFlow()

    private val _detailState = MutableStateFlow<TransactionState<Transaction>>(TransactionState.Idle)
    val detailState = _detailState.asStateFlow()

    private val _actionState = MutableStateFlow<TransactionState<TransactionAction>>(TransactionState.Idle)
    val actionState = _actionState.asStateFlow()

    private val _createdTransactionId = MutableStateFlow<String?>(null)
    val createdTransactionId = _createdTransactionId.asStateFlow()

    fun createTransaction(transaction: Transaction) {
        if (_createdTransactionId.value != null) return
        viewModelScope.launch {
            _actionState.value = TransactionState.Loading
            transactionUseCases.createTransactionUseCase(transaction)
                .onSuccess { id ->
                    _createdTransactionId.value = id
                    _actionState.value = TransactionState.Success(TransactionAction.INITIALIZE)
                }
                .onFailure { _actionState.value = TransactionState.Error(it.message ?: "Error") }
        }
    }

    fun prepareTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _actionState.value = TransactionState.Loading
            prepareTransactionUseCase(transaction)
                .onSuccess { id ->
                    _createdTransactionId.value = id
                    _actionState.value = TransactionState.Success(TransactionAction.INITIALIZE)
                }
                .onFailure { _actionState.value = TransactionState.Error(it.message ?: "Error") }
        }
    }

    fun confirmPayment(
        bookingId: String,
        transactionId: String,
        brand: PaymentBrand,
        title: String?,
        message: String?
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModelScope.launch {
            _actionState.value = TransactionState.Loading
            completeBookingPaymentUseCase(bookingId, transactionId, brand)
                .onSuccess {
                    timerManager.stopTimer()
                    handleNotifications(userId, bookingId, title, message)

                    _actionState.value = TransactionState.Success(TransactionAction.CONFIRM)
                }
                .onFailure { _actionState.value = TransactionState.Error(it.message ?: "Payment Failed") }
        }
    }

    private suspend fun handleNotifications(userId: String, bookingId: String, title: String?, message: String?) {
        if (title != null && message != null) {
            notificationUseCases.saveNotificationUseCase(userId, title, message, bookingId)
            notificationHelper.showBookingNotification(title, message, bookingId)
        }
    }

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            _detailState.value = TransactionState.Loading
            transactionUseCases.getTransactionByIdUseCase(id).onSuccess {
                _detailState.value = if (it != null) TransactionState.Success(it) else TransactionState.Error("Not Found")
            }.onFailure { _detailState.value = TransactionState.Error(it.message ?: "Error") }
        }
    }

    fun recoverTransactionId(bookingId: String) {
        viewModelScope.launch {
            transactionUseCases.getTransactionByBookingIdUseCase(bookingId).onSuccess { transaction ->
                if (transaction != null) _createdTransactionId.value = transaction.id
            }
        }
    }

    fun clearCreatedId() { _createdTransactionId.value = null }

    fun resetActionState() { _actionState.value = TransactionState.Idle }
}