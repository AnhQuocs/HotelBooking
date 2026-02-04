package com.example.hotelbooking.features.transaction.presentation.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.presentation.ui.getPaymentBrandIcon
import com.example.hotelbooking.features.transaction.presentation.ui.getStatusColor
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.ErrorRed
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    state: TransactionState<Transaction>, onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        when (state) {
            is TransactionState.Loading, TransactionState.Idle -> {
                Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is TransactionState.Error -> Box(
                Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) { Text(stringResource(id = R.string.error, state.message), color = ErrorRed) }

            is TransactionState.Success -> {
                val transaction = state.data
                TransactionDetailContent(
                    transaction = transaction
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(Dimen.SizeSM)
                    .clickable { onBackClick() })

            Text(
                text = stringResource(id = R.string.transaction_detail),
                style = AfacadTypography.titleLarge.copy(
                    fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White
                )
            )

            Spacer(modifier = Modifier.size(Dimen.SizeSM))
        }
    }
}

@Composable
fun TransactionDetailContent(transaction: Transaction) {
    val statusColor = getStatusColor(transaction.status)

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightXXL)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(statusColor, statusColor.copy(alpha = 0.7f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimen.HeightML))

            Icon(
                imageVector = getStatusIcon(transaction.status),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .padding(Dimen.PaddingSM)
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Text(
                text = getStatusText(transaction.status),
                style = AfacadTypography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(id = R.string.total_payment),
                            style = AfacadTypography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", transaction.totalPrice)}",
                            style = AfacadTypography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = BlueNavy
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    InfoRowCopyable(
                        label = stringResource(id = R.string.transaction_id), value = transaction.id
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.M))
                    InfoRow(
                        label = stringResource(id = R.string.booking_id),
                        value = transaction.bookingId
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(id = R.string.payment_method2),
                            style = AfacadTypography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        transaction.paymentMethod?.let {
                            Image(
                                painter = painterResource(id = getPaymentBrandIcon(it)),
                                contentDescription = null,
                                modifier = Modifier.height(Dimen.HeightXS)
                            )
                        }
                        Spacer(modifier = Modifier.width(AppSpacing.S))
                        Text(
                            text = transaction.paymentMethod?.name
                                ?: stringResource(id = R.string.e_wallet),
                            style = AfacadTypography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    Text(
                        stringResource(id = R.string.transaction_history),
                        style = AfacadTypography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    TimelineStep(
                        time = transaction.createdAt,
                        title = stringResource(id = R.string.transaction_created),
                        isCompleted = true,
                        isLast = transaction.status == TransactionStatus.PENDING
                    )

                    when (transaction.status) {
                        TransactionStatus.PAID -> {
                            TimelineStep(
                                time = transaction.updatedAt,
                                title = stringResource(id = R.string.payment_success),
                                isCompleted = true,
                                color = AvailableGreen,
                                isLast = true
                            )
                        }

                        TransactionStatus.CANCELLED -> {
                            TimelineStep(
                                time = transaction.updatedAt,
                                title = stringResource(id = R.string.transaction_cancelled),
                                isCompleted = true,
                                color = CancelledRed,
                                isLast = true
                            )
                        }

                        TransactionStatus.REFUND -> {
                            TimelineStep(
                                time = transaction.updatedAt,
                                title = stringResource(id = R.string.payment_success),
                                isCompleted = true,
                                isLast = false
                            )
                            TimelineStep(
                                time = transaction.refundedAt ?: 0L,
                                title = stringResource(id = R.string.refund_success),
                                isCompleted = true,
                                color = Color(0xFF9C27B0),
                                isLast = true
                            )
                        }

                        else -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))
            OutlinedButton(
                onClick = {
                    /* Contact Support */
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null)
                Spacer(modifier = Modifier.width(AppSpacing.S))
                Text(stringResource(id = R.string.incident_report))
            }
        }
    }
}