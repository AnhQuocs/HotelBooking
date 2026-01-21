package com.example.hotelbooking.features.notification.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hotelbooking.R
import com.example.hotelbooking.features.notification.domain.usecase.SaveNotificationUseCase
import com.example.hotelbooking.features.notification.presentation.ui.NotificationActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveNotificationUseCase: SaveNotificationUseCase
) {
    private val CHANNEL_ID = "booking_channel_id"
    private val CHANNEL_NAME = "Booking Notifications"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Booking status notification"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showBookingNotification(
        userId: String,
        title: String,
        message: String,
        bookingId: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            saveNotificationUseCase(
                userId = userId,
                title = title,
                message = message,
                bookingId = bookingId
            )
        }

        val intent = Intent(context, NotificationActivity::class.java).apply {
            putExtra("booking_id", bookingId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.hotel_booking_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }
}