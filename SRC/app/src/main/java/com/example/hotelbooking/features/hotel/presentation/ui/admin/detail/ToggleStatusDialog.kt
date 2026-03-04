package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun ToggleStatusDialog(
    isCurrentlyActive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isCurrentlyActive)
                    stringResource(R.string.hotel_suspend_title)
                else
                    stringResource(R.string.hotel_activate_title)
            )
        },
        text = {
            Text(
                text = if (isCurrentlyActive)
                    stringResource(R.string.hotel_suspend_message)
                else
                    stringResource(R.string.hotel_activate_message)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentlyActive) Color.Red else AvailableGreen
                )
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.cancel),
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
fun NoRoomsWarningDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.cannot_activate_title))
        },
        text = {
            Text(stringResource(R.string.cannot_activate_message))
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text(stringResource(R.string.understood))
            }
        }
    )
}