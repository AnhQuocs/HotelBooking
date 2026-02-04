package com.example.hotelbooking.features.transaction.presentation.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.transaction.presentation.ui.detail.TransactionDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionHistoryActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current

            TransactionHistoryScreen(
                onDetailClick = { id ->
                    val intent = Intent(
                        context,
                        TransactionDetailActivity::class.java
                    ).putExtra("transactionId", id)
                    context.startActivity(intent)
                },
                onBackClick = { finish() }
            )
        }
    }
}