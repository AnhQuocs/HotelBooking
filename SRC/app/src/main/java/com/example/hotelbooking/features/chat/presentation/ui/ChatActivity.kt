package com.example.hotelbooking.features.chat.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hotelbooking.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""
        val hotelName = intent.getStringExtra("hotelName") ?: ""
        val shortAddress = intent.getStringExtra("shortAddress") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""

        setContent {

        }
    }
}