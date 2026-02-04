package com.example.hotelbooking.features.hotel.presentation.ui.user.popular

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.hotel.presentation.ui.user.AllHotelsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllHotelsActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AllHotelsScreen(onBackClick = { finish() })
        }
    }
}