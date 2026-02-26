package com.example.hotelbooking.features.home.admin.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.features.home.admin.ui.ActionDialog
import com.example.hotelbooking.features.home.admin.ui.dashboard.ConfirmDialogState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.CancelledRed
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AdminActionBottomBar(
    booking: Booking,
    viewModel: AdminBookingDetailViewModel,
    isProcessing: Boolean
) {
    val zoneId = ZoneId.systemDefault()
    val now = LocalDate.now()
    val checkOutDate = booking.endDate.toDate().toInstant().atZone(zoneId).toLocalDate()

    val isEarlyCheckOut = now.isBefore(checkOutDate)
    val checkInDate = booking.startDate.toDate().toInstant().atZone(zoneId).toLocalDate()
    val isArrivalDay = !now.isBefore(checkInDate)

    var confirmDialogState by remember { mutableStateOf<ConfirmDialogState?>(null) }

    confirmDialogState?.let { state ->
        ActionDialog(
            titleRes = state.titleRes,
            messageRes = state.messageRes,
            onDismiss = { confirmDialogState = null },
            onConfirm = {
                state.onConfirm()
                confirmDialogState = null
            }
        )
    }

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
        ) {
            if (booking.stayStatus == StayStatus.NONE && booking.status == BookingStatus.CONFIRMED) {
                if (isArrivalDay) {
                    OutlinedButton(
                        onClick = {
                            confirmDialogState = ConfirmDialogState(
                                titleRes = R.string.no_show,
                                messageRes = R.string.confirm_no_show,
                                onConfirm = {
                                    viewModel.markAsNoShow(booking.bookingId)
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CancelledRed)
                    ) {
                        Text(
                            stringResource(id = R.string.no_show)
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.waiting_for_arrival_date),
                        style = AfacadTypography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else if (booking.stayStatus == StayStatus.CHECK_IN) {
                Button(
                    onClick = {
                        confirmDialogState = ConfirmDialogState(
                            titleRes = R.string.check_out,
                            messageRes = R.string.confirm_check_out,
                            onConfirm = {
                                viewModel.processCheckOut(booking)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEarlyCheckOut) Color(0xFFFFA000) else AvailableGreen
                    )
                ) {
                    Text(
                        if (isEarlyCheckOut) stringResource(id = R.string.early_check_out) else stringResource(
                            id = R.string.confirm_check_out
                        )
                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.status) + ": ${booking.stayStatus.name}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}