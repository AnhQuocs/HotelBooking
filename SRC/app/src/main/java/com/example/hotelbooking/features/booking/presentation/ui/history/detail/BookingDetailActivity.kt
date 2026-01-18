package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.booking.presentation.ui.rebook.RebookActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""
        val roomId = intent.getStringExtra("roomId") ?: ""

        setContent {
            val context = LocalContext.current

            BookingDetailScreen(
                onBackClick = { finish() },
                bookingId = bookingId,
                roomId = roomId,
                onRebook = {
                    val intent = Intent(context, RebookActivity::class.java)
                        .putExtra("bookingId", bookingId)
                    context.startActivity(intent)
                }
            )
        }
    }
}