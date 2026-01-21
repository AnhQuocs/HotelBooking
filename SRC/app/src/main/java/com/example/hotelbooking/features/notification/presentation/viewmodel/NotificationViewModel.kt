package com.example.hotelbooking.features.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.notification.domain.model.BookingNotification
import com.example.hotelbooking.features.notification.domain.usecase.NotificationUseCases
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUseCases: NotificationUseCases,
) : ViewModel() {

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private val _notifications = MutableStateFlow<List<BookingNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    init {
        observeNotifications()
    }

    private fun observeNotifications() {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            notificationUseCases.getNotificationsUseCase(uid).collect { list ->
                _notifications.value = list

                _unreadCount.value = list.count { !it.isRead }
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationUseCases.markNotificationAsReadUseCase(notificationId)
        }
    }
}