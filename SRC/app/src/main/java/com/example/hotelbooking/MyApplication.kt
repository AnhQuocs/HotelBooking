package com.example.hotelbooking

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings

        val config = mapOf(
            "cloud_name" to "daledsv1v",
            "api_key" to "642531578175184",
            "api_secret" to "4bVEOj0JCpZQHshihxfpLs2oDkE",
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}