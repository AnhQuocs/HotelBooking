package com.example.hotelbooking

import android.app.Application
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
    }
}