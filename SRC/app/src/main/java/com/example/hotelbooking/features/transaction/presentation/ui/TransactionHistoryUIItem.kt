package com.example.hotelbooking.features.transaction.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.IndigoBlue
import com.example.hotelbooking.ui.theme.OrangeVibrant
import com.example.hotelbooking.ui.theme.RatingYellow
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.example.hotelbooking.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatusChip(status: TransactionStatus) {
    val baseColor = getStatusColor(status)

    Surface(
        color = baseColor.copy(alpha = 0.1f),
        contentColor = baseColor,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, baseColor.copy(alpha = 0.2f))
    ) {
        Text(
            text = status.name,
            style = AfacadTypography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = Dimen.PaddingSM,
                vertical = Dimen.PaddingXSPlus
            )
        )
    }
}

@Composable
fun QuickStatsRow(transactions: List<Transaction>) {
    val totalPaid = transactions
        .filter { it.status == TransactionStatus.PAID }
        .sumOf { it.amountPaid }

    val pendingCount = transactions
        .count { it.status == TransactionStatus.PENDING }

    val totalRefund = transactions
        .filter { it.status == TransactionStatus.REFUND }
        .sumOf { it.amountPaid }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ModernStatCard(
            title = stringResource(R.string.revenue),
            value = NumberFormat
                .getCurrencyInstance(Locale.US)
                .format(totalPaid),
            icon = Icons.Default.MonetizationOn,
            startColor = AvailableGreen,
            endColor = SuccessGreen
        )

        if (pendingCount > 0) {
            ModernStatCard(
                title = stringResource(R.string.pending),
                value = stringResource(R.string.order_count, pendingCount),
                icon = Icons.Default.HourglassEmpty,
                startColor = RatingYellow,
                endColor = OrangeVibrant
            )
        }

        if (totalRefund > 0.0) {
            ModernStatCard(
                title = stringResource(R.string.refunded),
                value = NumberFormat
                    .getCurrencyInstance(Locale.US)
                    .format(totalRefund),
                icon = Icons.Default.Restore,
                startColor = IndigoBlue,
                endColor = RoyalBlue
            )
        }
    }
}

@Composable
fun ModernStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp),
        shape = RoundedCornerShape(AppShape.ShapeXL),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(Dimen.SizeMega)
                    .offset(x = 20.dp, y = 20.dp)
                    .rotate(-15f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimen.PaddingM),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.S))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(top = Dimen.PaddingS)
                )
            }
        }
    }
}