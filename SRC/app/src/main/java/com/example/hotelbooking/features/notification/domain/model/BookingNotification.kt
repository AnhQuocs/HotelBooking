package com.example.hotelbooking.features.notification.domain.model

data class BookingNotification(
    val id: Long = 0,
    val title: String,
    val message: String,
    val bookingId: String,
    val timestamp: Long,
    val isRead: Boolean = false
)