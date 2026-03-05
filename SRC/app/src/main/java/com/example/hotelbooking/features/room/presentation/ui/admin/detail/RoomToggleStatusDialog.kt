package com.example.hotelbooking.features.room.presentation.ui.admin.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen

@Composable
fun RoomToggleStatusDialog(
    isCurrentlyActive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    if (isCurrentlyActive)
                        R.string.stop_selling_room_type
                    else
                        R.string.open_selling_room_type
                ),
                style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = stringResource(
                    if (isCurrentlyActive)
                        R.string.hide_room_type_confirm
                    else
                        R.string.show_room_type_confirm
                ),
                style = AfacadTypography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentlyActive) Color.Red else AvailableGreen
                )
            ) {
                Text(stringResource(id = R.string.confirm), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel), color = Color.Gray)
            }
        }
    )
}