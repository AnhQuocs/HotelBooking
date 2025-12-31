package com.example.hotelbooking.features.booking.presentation.ui.history

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hotelbooking.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""
        val roomId = intent.getStringExtra("roomId") ?: ""

        setContent {
            BookingDetailScreen(
                onBackClick = { finish() },
                bookingId = bookingId,
                roomId = roomId
            )
        }
    }
}