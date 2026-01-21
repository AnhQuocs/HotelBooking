package com.example.hotelbooking.features.notification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hotelbooking.features.notification.domain.model.BookingNotification

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val bookingId: String,
    val timestamp: Long,
    val isRead: Boolean
)

fun NotificationEntity.toDomain() = BookingNotification(
    id = id,
    userId = userId,
    title = title,
    message = message,
    bookingId = bookingId,
    timestamp = timestamp,
    isRead = isRead
)

fun BookingNotification.toEntity() = NotificationEntity(
    id = id,
    title = title,
    userId = userId,
    message = message,
    bookingId = bookingId,
    timestamp = timestamp,
    isRead = isRead
)