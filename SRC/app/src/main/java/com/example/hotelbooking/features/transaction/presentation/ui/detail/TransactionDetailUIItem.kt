package com.example.hotelbooking.features.transaction.presentation.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineStep(
    time: Long,
    title: String,
    isCompleted: Boolean,
    isLast: Boolean,
    color: Color = BlueNavy
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(time)),
                style = AfacadTypography.labelSmall,
                color = Color.Gray
            )
            if (!isLast) {
                VerticalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Dimen.PaddingXS),
                    color = Color.LightGray
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = Dimen.PaddingS)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (isCompleted) color else Color.LightGray, CircleShape)
            )
        }

        Column(modifier = Modifier.padding(bottom = Dimen.PaddingL)) {
            Text(
                text = title,
                style = AfacadTypography.bodyMedium,
                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = if (isCompleted) Color.Black else Color.Gray
            )
            Text(
                text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(time)),
                style = AfacadTypography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun InfoRowCopyable(label: String, value: String) {
    val clipboardManager = LocalClipboardManager.current

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = AfacadTypography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                clipboardManager.setText(AnnotatedString(value))
            }
        ) {
            Text(
                text = value.take(8).uppercase() + "...",
                style = AfacadTypography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(AppSpacing.XS))
            Icon(
                Icons.Default.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = BlueNavy
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = AfacadTypography.bodyMedium, color = Color.Gray)
        Text(text = value, style = AfacadTypography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

fun getStatusIcon(status: TransactionStatus): ImageVector = when (status) {
    TransactionStatus.PAID -> Icons.Default.CheckCircle
    TransactionStatus.PENDING -> Icons.Default.HourglassEmpty
    TransactionStatus.CANCELLED -> Icons.Default.Cancel
    TransactionStatus.REFUND -> Icons.Default.Restore
}

@Composable
fun getStatusText(status: TransactionStatus): String = when (status) {
    TransactionStatus.PAID -> stringResource(id = R.string.payment_success)
    TransactionStatus.PENDING -> stringResource(id = R.string.pending2)
    TransactionStatus.CANCELLED -> stringResource(id = R.string.cancelled_transaction)
    TransactionStatus.REFUND -> stringResource(id = R.string.refunded)
}