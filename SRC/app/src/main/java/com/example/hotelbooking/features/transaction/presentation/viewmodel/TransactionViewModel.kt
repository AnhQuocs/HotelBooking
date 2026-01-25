package com.example.hotelbooking.features.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.notification.domain.usecase.NotificationUseCases
import com.example.hotelbooking.features.notification.util.NotificationHelper
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.domain.usecase.CompleteBookingPaymentUseCase
import com.example.hotelbooking.features.transaction.domain.usecase.TransactionUseCases
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TransactionState<out T> {
    object Idle : TransactionState<Nothing>()
    object Loading : TransactionState<Nothing>()
    data class Success<T>(val data: T) : TransactionState<T>()
    data class Error(val message: String) : TransactionState<Nothing>()
}

class TransactionViewModel(
    private val transactionUseCases: TransactionUseCases,
    private val completeBookingPaymentUseCase: CompleteBookingPaymentUseCase,
    private val notificationUseCases: NotificationUseCases,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _historyState = MutableStateFlow<TransactionState<List<Transaction>>>(TransactionState.Idle)
    val historyState = _historyState.asStateFlow()

    private val _detailState = MutableStateFlow<TransactionState<Transaction>>(TransactionState.Idle)
    val detailState = _detailState.asStateFlow()

    private val _actionState = MutableStateFlow<TransactionState<Unit>>(TransactionState.Idle)
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
                    _actionState.value = TransactionState.Success(Unit)
                }
                .onFailure { _actionState.value = TransactionState.Error(it.message ?: "Error") }
        }
    }

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            _detailState.value = TransactionState.Loading
            transactionUseCases.getTransactionByIdUseCase(id)
                .onSuccess {
                    if (it != null) _detailState.value = TransactionState.Success(it)
                    else _detailState.value = TransactionState.Error("Not Found")
                }
                .onFailure { _detailState.value = TransactionState.Error(it.message ?: "Error") }
        }
    }

    fun recoverTransactionId(bookingId: String) {
        viewModelScope.launch {
            transactionUseCases.getTransactionByBookingIdUseCase(bookingId)
                .onSuccess { transaction ->
                    if (transaction != null) {
                        _createdTransactionId.value = transaction.id
                    }
                }
        }
    }

    fun getTransactionsByUserId(userId: String) {
        viewModelScope.launch {
            _historyState.value = TransactionState.Loading
            transactionUseCases.getUserTransactionsUseCase(userId)
                .onSuccess { _historyState.value = TransactionState.Success(it) }
                .onFailure { _historyState.value = TransactionState.Error(it.message ?: "Error") }
        }
    }

    fun updateStatus(id: String, status: TransactionStatus) {
        viewModelScope.launch {
            _actionState.value = TransactionState.Loading
            transactionUseCases.updateTransactionStatusUseCase(id, status)
                .onSuccess { _actionState.value = TransactionState.Success(Unit) }
                .onFailure { _actionState.value = TransactionState.Error(it.message ?: "Update Failed") }
        }
    }

    fun confirmPayment(
        bookingId: String,
        transactionId: String,
        title: String?,
        message: String?
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        viewModelScope.launch {
            _actionState.value = TransactionState.Loading
            completeBookingPaymentUseCase(bookingId, transactionId)
                .onSuccess {
                    _actionState.value = TransactionState.Success(Unit)

                    title?.let {
                        message?.let { it1 ->
                            notificationUseCases.saveNotificationUseCase(
                                userId = userId,
                                title = it,
                                message = it1,
                                bookingId = bookingId,
                            )
                        }
                    }

                    title?.let {
                        message?.let { it1 ->
                            notificationHelper.showBookingNotification(
                                title = it,
                                message = it1,
                                bookingId = bookingId
                            )
                        }
                    }
                }
                .onFailure { _actionState.value = TransactionState.Error(it.message ?: "Payment Failed") }
        }
    }
}