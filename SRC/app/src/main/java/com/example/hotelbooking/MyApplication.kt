package com.example.hotelbooking

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings

//        setupPeriodicCleanup()
    }

//    private fun setupPeriodicCleanup() {
//        val cleanupRequest = PeriodicWorkRequestBuilder<BookingCleanupWorker>(
//            15, TimeUnit.MINUTES
//        ).build()
//
//        // Đẩy vào hàng đợi của hệ thống
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "CleanupWorker",
//            ExistingPeriodicWorkPolicy.KEEP,
//            cleanupRequest
//        )
//    }
}