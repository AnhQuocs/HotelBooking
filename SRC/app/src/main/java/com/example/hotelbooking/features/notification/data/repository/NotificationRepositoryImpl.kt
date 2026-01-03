package com.example.hotelbooking.features.notification.data.repository

import com.example.hotelbooking.features.notification.data.local.dao.NotificationDao
import com.example.hotelbooking.features.notification.data.local.entity.toDomain
import com.example.hotelbooking.features.notification.data.local.entity.toEntity
import com.example.hotelbooking.features.notification.domain.model.BookingNotification
import com.example.hotelbooking.features.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl (
    private val dao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<BookingNotification>> {
        return dao.getAllNotifications().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNotification(notification: BookingNotification) {
        dao.insertNotification(notification.toEntity())
    }

    override suspend fun markAsRead(notificationId: Long) {
        dao.markAsRead(notificationId)
    }

    override suspend fun countUnread(): Int {
        return dao.countUnread()
    }
}