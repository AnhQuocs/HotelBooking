package com.example.hotelbooking.features.home.ui.admin.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.features.home.ui.admin.ActionDialog
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.ArrivalBlue
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.NearBlack
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class ConfirmDialogState(
    @StringRes val titleRes: Int, @StringRes val messageRes: Int, val onConfirm: () -> Unit
)

@Composable
fun AdminBookingItemCard(
    booking: Booking,
    filterType: BookingFilterType,
    onClick: () -> Unit,
    adminBookingDetailViewModel: AdminBookingDetailViewModel
) {
    val guestText = if (booking.numberOfGuests > 1) stringResource(id = R.string.guests)
    else stringResource(id = R.string.guest)

    var confirmDialogState by remember { mutableStateOf<ConfirmDialogState?>(null) }

    confirmDialogState?.let { state ->
        ActionDialog(
            titleRes = state.titleRes,
            messageRes = state.messageRes,
            onDismiss = { confirmDialogState = null },
            onConfirm = {
                state.onConfirm()
                confirmDialogState = null
            })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeM)
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingM),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${booking.bookingId.takeLast(6).uppercase()}",
                    style = AfacadTypography.labelLarge,
                    color = Color.Gray
                )

                StatusBadge(bookingStatus = booking.status, stayStatus = booking.stayStatus)
            }

            Text(
                text = booking.guests.firstOrNull()?.fullName ?: "Unknown Guest",
                style = AfacadTypography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NearBlack
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeS),
                    tint = Color.Gray
                )
                Text(
                    text = stringResource(id = R.string.room) + ": ${booking.roomNumber}",
                    style = AfacadTypography.bodyMedium,
                    color = NearBlack
                )
                Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeS),
                    tint = Color.Gray
                )
                Text(
                    text = " ${booking.numberOfGuests}" + guestText,
                    style = AfacadTypography.bodyMedium,
                    color = NearBlack
                )
            }

            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            Box(modifier = Modifier.fillMaxWidth()) {
                when (filterType) {
                    BookingFilterType.ARRIVALS -> {
                        if (booking.stayStatus == StayStatus.NONE) {
                            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)) {
                                OutlinedButton(
                                    onClick = {
                                        confirmDialogState = ConfirmDialogState(
                                            titleRes = R.string.no_show,
                                            messageRes = R.string.confirm_no_show,
                                            onConfirm = {
                                                adminBookingDetailViewModel.markAsNoShow(booking.bookingId)
                                            })
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CancelledRed),
                                    modifier = Modifier.weight(0.6f)
                                ) {
                                    Text(stringResource(id = R.string.no_show))
                                }
                            }
                        }
                    }

                    BookingFilterType.DEPARTURES -> {
                        if (booking.stayStatus == StayStatus.CHECK_IN) {
                            Button(
                                onClick = {
                                    confirmDialogState = ConfirmDialogState(
                                        titleRes = R.string.check_out,
                                        messageRes = R.string.confirm_check_out,
                                        onConfirm = {
                                            adminBookingDetailViewModel.processCheckOut(booking)
                                        })
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(id = R.string.confirm_check_out))
                            }
                        }
                    }

                    BookingFilterType.OCCUPANCY -> {
                        if (booking.stayStatus == StayStatus.CHECK_IN) {
                            Button(
                                onClick = {
                                    confirmDialogState = ConfirmDialogState(
                                        titleRes = R.string.early_check_out,
                                        messageRes = R.string.confirm_early_checkout,
                                        onConfirm = {
                                            adminBookingDetailViewModel.processCheckOut(booking)
                                        })
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(id = R.string.early_check_out))
                            }
                        }
                    }

                    BookingFilterType.REVENUE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.transaction_amount),
                                style = AfacadTypography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "+$${String.format(Locale.US, "%,.0f", booking.totalPrice)}",
                                style = AfacadTypography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AvailableGreen
                            )
                        }
                    }

                    BookingFilterType.NEW_BOOKINGS -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = stringResource(
                                    id = R.string.booked_at, adminFormatTimestamp(booking.createdAt)
                                ), style = AfacadTypography.bodySmall, color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(bookingStatus: BookingStatus, stayStatus: StayStatus) {
    val (color, textRes) = when {
        bookingStatus == BookingStatus.CANCELLED -> {
            CancelledRed to R.string.status_cancelled
        }

        bookingStatus == BookingStatus.PENDING -> {
            Color(0xFFFFA000) to R.string.status_pending
        }

        else -> {
            when (stayStatus) {
                StayStatus.NONE -> Color.Gray to R.string.stay_waiting
                StayStatus.CHECK_IN -> ArrivalBlue to R.string.stay_in_house
                StayStatus.CHECK_OUT -> AvailableGreen to R.string.stay_checked_out
                StayStatus.NO_SHOW -> CancelledRed to R.string.stay_no_show
            }
        }
    }

    Surface(
        color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(AppShape.ShapeXXS)
    ) {
        Text(
            text = stringResource(textRes),
            modifier = Modifier.padding(horizontal = Dimen.PaddingS, vertical = Dimen.PaddingXS),
            color = color,
            style = AfacadTypography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

fun adminFormatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}