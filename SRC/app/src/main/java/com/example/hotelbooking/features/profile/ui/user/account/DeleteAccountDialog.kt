package com.example.hotelbooking.features.profile.ui.user.account

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.theme.AfacadTypography

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color.Red
            )
        },
        title = {
            Text(
                text = stringResource(R.string.delete_account_title),
                style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_account_message),
                style = AfacadTypography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text(
                    stringResource(id = R.string.confirm),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(id = R.string.cancel),
                    color = Color.Gray
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(AppShape.ShapeL)
    )
}