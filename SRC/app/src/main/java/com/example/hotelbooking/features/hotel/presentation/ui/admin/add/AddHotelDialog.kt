package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun ExitDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = Color.White, title = {
            Text(
                text = stringResource(R.string.save_draft_title),
                style = AfacadTypography.titleLarge.copy(color = Color.Black)
            )
        }, text = {
            Text(
                text = stringResource(R.string.save_draft_message),
                style = AfacadTypography.bodyMedium,
                color = Color.Black
            )
        }, confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
                modifier = Modifier
                    .padding(horizontal = Dimen.PaddingS)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalBlue
                ),
                shape = RoundedCornerShape(AppShape.ShapeM)
            ) {
                Text(
                    text = stringResource(R.string.save_draft_confirm), color = Color.White
                )
            }
        }, dismissButton = {
            TextButton(
                onClick = onDismiss, modifier = Modifier.padding(horizontal = Dimen.PaddingS)
            ) {
                Text(
                    text = stringResource(R.string.save_draft_dismiss), color = RoyalBlue
                )
            }
        }, shape = RoundedCornerShape(AppShape.ShapeXL)
    )
}

@Composable
fun SaveDraftDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = Color.White, title = {
            Text(
                text = stringResource(R.string.save_draft_title),
                style = AfacadTypography.titleLarge.copy(color = Color.Black)
            )
        }, text = {
            Text(
                text = stringResource(R.string.save_draft_question),
                style = AfacadTypography.bodyMedium,
                color = Color.Black
            )
        }, confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
                modifier = Modifier
                    .padding(horizontal = Dimen.PaddingS)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalBlue
                ),
                shape = RoundedCornerShape(AppShape.ShapeM)
            ) {
                Text(
                    text = stringResource(R.string.save_draft_confirm), color = Color.White
                )
            }
        }, dismissButton = {
            TextButton(
                onClick = onDismiss, modifier = Modifier.padding(horizontal = Dimen.PaddingS)
            ) {
                Text(
                    text = stringResource(R.string.cancel), color = RoyalBlue
                )
            }
        }, shape = RoundedCornerShape(AppShape.ShapeXL)
    )
}