package com.example.hotelbooking.features.transaction.presentation.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.presentation.ui.getPaymentBrandIcon
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionHistoryScreen(
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    onDetailClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by transactionViewModel.historyState.collectAsState()

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        transactionViewModel.getTransactions(userId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.transactions),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimen.PaddingM)
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.S))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when (val currentState = state) {
                    is TransactionState.Loading, TransactionState.Idle -> item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }

                    is TransactionState.Success -> {
                        val transactions = currentState.data
                        item {
                            val list = currentState.data
                            Text(
                                stringResource(id = R.string.transaction_statistics),
                                style = AfacadTypography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            QuickStatsRow(list)
                        }

                        items(transactions) { item ->
                            TransactionItem(item, onClick = { onDetailClick(item.id) })
                        }
                    }

                    is TransactionState.Error -> item { Text(stringResource(id = R.string.error, currentState.message)) }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingSM)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            transaction.paymentMethod?.let {
                Image(
                    painter = painterResource(id = getPaymentBrandIcon(it)),
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeXLPlus)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Mã: ${transaction.id.takeLast(8)}", fontWeight = FontWeight.Bold)
                Text(
                    text = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date(transaction.createdAt)),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${transaction.totalPrice}",
                    fontWeight = FontWeight.ExtraBold,
                    color = BlueNavy
                )
                StatusChip(status = transaction.status)
            }
        }
    }
}