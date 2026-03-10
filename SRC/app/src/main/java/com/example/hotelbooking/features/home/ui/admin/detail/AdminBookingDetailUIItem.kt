package com.example.hotelbooking.features.home.ui.admin.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.features.home.ui.admin.ActionDialog
import com.example.hotelbooking.features.home.ui.admin.dashboard.ConfirmDialogState
import com.example.hotelbooking.features.home.ui.admin.dashboard.adminFormatTimestamp
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.BrightBlue
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.SlateGray
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
                            titleRes = if (isEarlyCheckOut) R.string.early_check_out else R.string.check_out,
                            messageRes = if (isEarlyCheckOut) R.string.confirm_early_checkout else R.string.confirm_check_out,
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

@Composable
fun AdminBookingSummaryCard(
    booking: Booking,
    hotel: Hotel?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${booking.bookingId.uppercase()}",
                    style = AfacadTypography.labelMedium,
                    color = SlateGray
                )
                Text(
                    text = adminFormatTimestamp(booking.createdAt),
                    style = AfacadTypography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Text(
                text = hotel?.name ?: "N/A",
                style = AfacadTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NearBlack
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = AppSpacing.M),
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateTimeItem(
                    label = stringResource(id = R.string.check_in),
                    date = adminFormatTimestamp(booking.startDate),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = BlueNavy,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(horizontal = 4.dp)
                )
                DateTimeItem(
                    label = stringResource(id = R.string.check_out),
                    date = adminFormatTimestamp(booking.endDate),
                    modifier = Modifier.weight(1f),
                    textAlign = Alignment.End
                )
            }
        }
    }
}

@Composable
fun AdminRoomPaymentCard(
    booking: Booking,
    room: RoomType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Text(
                text = stringResource(id = R.string.room_and_payment),
                style = AfacadTypography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BlueNavy
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            InfoRow(label = stringResource(id = R.string.room_type), value = room.name)
            InfoRow(
                label = stringResource(id = R.string.room_number),
                value = "#${booking.roomNumber}"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = AppSpacing.S),
                thickness = 0.5.dp,
                color = Color(0xFFF1F1F1)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.total_price),
                    style = AfacadTypography.bodyLarge
                )
                Text(
                    text = "$${booking.totalPrice}",
                    style = AfacadTypography.titleLarge,
                    color = AvailableGreen,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun AdminGuestInfoCard(
    booking: Booking
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Text(
                text = stringResource(id = R.string.guest_information) + " (${booking.numberOfGuests})",
                style = AfacadTypography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            booking.guests.forEachIndexed { index, guest ->
                val isRep = guest.isRepresentative

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (index == 0) Dimen.PaddingS else Dimen.PaddingM)
                        .background(
                            if (isRep) BrightBlue.copy(alpha = 0.05f) else Color.Transparent,
                            RoundedCornerShape(AppShape.ShapeS)
                        )
                        .padding(if (isRep) 8.dp else 0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isRep) Icons.Default.Star else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isRep) BrightBlue else SlateGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.S))
                        Text(
                            text = guest.fullName + if (isRep) " (${stringResource(id = R.string.guest_representative)})" else "",
                            style = AfacadTypography.bodyLarge,
                            fontWeight = if (isRep) FontWeight.Bold else FontWeight.Medium,
                            color = if (isRep) BrightBlue else NearBlack
                        )
                    }

                    GuestDetailItem(Icons.Default.Email, guest.email)
                    GuestDetailItem(Icons.Default.Phone, guest.phone)
                }

                if (index != booking.guests.lastIndex && !isRep) {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = Dimen.PaddingS),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun DateTimeItem(
    label: String,
    date: String,
    modifier: Modifier = Modifier,
    textAlign: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = textAlign) {
        Text(text = label, style = AfacadTypography.labelSmall, color = Color.Gray)
        Text(text = date, style = AfacadTypography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = AfacadTypography.bodyMedium, color = SlateGray)
        Text(text = value, style = AfacadTypography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun GuestDetailItem(icon: ImageVector, text: String?) {
    text?.let {
        Row(
            modifier = Modifier.padding(start = 28.dp, top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
            Spacer(Modifier.width(4.dp))
            Text(text = it, style = AfacadTypography.bodySmall, color = SlateGray)
        }
    }
}