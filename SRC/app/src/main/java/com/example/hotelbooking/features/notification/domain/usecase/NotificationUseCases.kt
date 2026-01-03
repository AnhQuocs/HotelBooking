package com.example.hotelbooking.features.notification.domain.usecase

import com.example.hotelbooking.features.notification.domain.model.BookingNotification
import com.example.hotelbooking.features.notification.domain.repository.NotificationRepository

data class NotificationUseCases(
    val saveNotificationUseCase: SaveNotificationUseCase,
    val getNotificationsUseCase: GetNotificationsUseCase,
    val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
)

class SaveNotificationUseCase (
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        title: String,
        message: String,
        bookingId: String
    ) {
        val notification = BookingNotification(
            title = title,
            message = message,
            bookingId = bookingId,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        repository.insertNotification(notification)
    }
}

class GetNotificationsUseCase (
    private val repository: NotificationRepository
) {
    operator fun invoke() = repository.getAllNotifications()
}

class MarkNotificationAsReadUseCase (
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(id: Long) = repository.markAsRead(id)
}