package com.example.hotelbooking.features.notification.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hotelbooking.features.notification.data.local.dao.NotificationDao
import com.example.hotelbooking.features.notification.data.local.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}