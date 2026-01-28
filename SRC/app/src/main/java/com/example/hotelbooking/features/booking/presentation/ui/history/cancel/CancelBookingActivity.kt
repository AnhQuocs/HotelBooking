package com.example.hotelbooking.features.booking.presentation.ui.history.cancel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CancelBookingActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""

        setContent {
            CancelBookingScreen(
                bookingId = bookingId,
                onBackClick = { finish() },
                onSuccess = {
                    lifecycleScope.launch {
                        BookingRefreshEvent.triggerRefresh()
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            )
        }
    }
}