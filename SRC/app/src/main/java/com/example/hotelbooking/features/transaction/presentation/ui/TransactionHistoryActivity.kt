package com.example.hotelbooking.features.transaction.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hotelbooking.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionHistoryActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TransactionHistoryScreen(
                onDetailClick = {

                },
                onBackClick = { finish() }
            )
        }
    }
}