package com.example.hotelbooking.features.notification.domain.repository

import com.example.hotelbooking.features.notification.domain.model.BookingNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<BookingNotification>>
    suspend fun insertNotification(notification: BookingNotification)
    suspend fun markAsRead(notificationId: Long)
    suspend fun countUnread(): Int
}