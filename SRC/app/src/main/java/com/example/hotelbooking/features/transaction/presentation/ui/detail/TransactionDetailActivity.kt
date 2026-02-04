package com.example.hotelbooking.features.transaction.presentation.ui.detail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val transactionId = intent.getStringExtra("transactionId") ?: ""

        setContent {
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            val state by transactionViewModel.detailState.collectAsState()

            LaunchedEffect(transactionId) {
                transactionViewModel.getTransactionById(transactionId)
            }

            TransactionDetailScreen(
                state = state,
                onBackClick = { finish() }
            )
        }
    }
}