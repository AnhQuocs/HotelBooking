package com.example.hotelbooking.features.transaction.presentation.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.presentation.ui.getPaymentBrandIcon
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.InputBackground
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SurfaceSoftBlue
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
            .padding(vertical = Dimen.PaddingXSPlus)
            .clickable { onClick() },
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, InputBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXXL)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                if (transaction.paymentMethod != null) {
                    Image(
                        painter = painterResource(id = getPaymentBrandIcon(transaction.paymentMethod)),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.id.takeLast(8).uppercase(),
                    style = AfacadTypography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                Text(
                    text = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
                        .format(Date(transaction.createdAt)),
                    style = AfacadTypography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format(Locale.US, "%.2f", transaction.totalPrice)}",
                    style = AfacadTypography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = BlueNavy
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                StatusChip(status = transaction.status)
            }
        }
    }
}