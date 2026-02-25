package com.example.hotelbooking.features.profile.feature.payment_card.presentation.ui.detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography

@Composable
fun DetailField(
    label: String,
    value: String,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, style = AfacadTypography.labelMedium, color = Color.Gray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = AfacadTypography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            trailingContent?.invoke()
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp)
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(AppShape.ShapeM),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimen.PaddingM),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(Dimen.SizeXL))
                Spacer(modifier = Modifier.height(AppSpacing.S))
                Text(
                    text = stringResource(R.string.loading_processing),
                    style = AfacadTypography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    lastFourDigits: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_card_title),
                style = AfacadTypography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_card_message,
                    lastFourDigits
                ),
                style = AfacadTypography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    style = AfacadTypography.labelLarge,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = AfacadTypography.labelLarge,
                    color = Color.Gray
                )
            }
        },
        shape = RoundedCornerShape(AppShape.ShapeL),
        containerColor = Color.White
    )
}

@Composable
fun DefaultCardWarningDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.cannot_delete_card_title),
                style = AfacadTypography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.cannot_delete_card_message),
                style = AfacadTypography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.understood),
                    style = AfacadTypography.labelLarge
                )
            }
        },
        shape = RoundedCornerShape(AppShape.ShapeL),
        containerColor = Color.White
    )
}