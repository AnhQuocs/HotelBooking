package com.example.hotelbooking.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SplashViewModel() : ViewModel() {

    fun checkUserRole(onResult: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        if (uid == null) {
            onResult("onboarding")
            return
        }

        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                val role = document.getString("role") ?: "USER"

                if (role == "ADMIN") {
                    onResult("admin_main")
                } else {
                    onResult("main")
                }
            } catch (e: Exception) {
                onResult("main")
            }
        }
    }
}