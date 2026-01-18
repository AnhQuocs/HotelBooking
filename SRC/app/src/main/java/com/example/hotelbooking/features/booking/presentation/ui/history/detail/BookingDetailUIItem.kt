package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.example.hotelbooking.ui.theme.SuccessGreen

@Composable
fun BookingDetailTopBar(
    onBackClick: () -> Unit,
    bookingStatus: BookingStatus?,
    onCancelClick: () -> Unit,
    onRebookClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
            .height(70.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = NearBlack,
                modifier = Modifier
                    .size(Dimen.SizeSM)
                    .clickable { onBackClick() }
            )

            Text(
                text = stringResource(id = R.string.booking_detail_title),
                style = AfacadTypography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NearBlack
                ),
            )

            Box {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .size(Dimen.SizeSM)
                        .clickable { showMenu = true },
                    tint = NearBlack
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    if (bookingStatus != null && bookingStatus != BookingStatus.CANCELLED) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(id = R.string.cancel_booking_confirm),
                                    style = AfacadTypography.bodyLarge.copy(color = Color.Red)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(Dimen.PaddingSM)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onCancelClick()
                            }
                        )
                    } else if (bookingStatus == BookingStatus.CANCELLED) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Re-order",
                                    style = AfacadTypography.bodyLarge.copy(color = PrimaryBlue)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Reorder,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(Dimen.PaddingSM)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onRebookClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmCancelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = stringResource(R.string.cancel_booking_title),
                style = AfacadTypography.titleLarge.copy(color = Color.Black)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.cancel_booking_message),
                style = AfacadTypography.bodyMedium,
                color = Color.Black
            )
        },
        confirmButton = {
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
                    text = stringResource(R.string.cancel_booking_confirm),
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = Dimen.PaddingS)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = RoyalBlue
                )
            }
        },
        shape = RoundedCornerShape(AppShape.ShapeXL)
    )
}

@Composable
fun BookingActions(
    booking: Booking,
    hotel: Hotel,
    isProcessing: Boolean,
    onAction: (StayStatus) -> Unit,
    onRebookClick: () -> Unit
) {
    val currentTime = System.currentTimeMillis()
    val checkInDateTime = combineDateAndTime(booking.startDate, hotel.checkInTime)
    val checkOutDateTime = combineDateAndTime(booking.endDate, hotel.checkOutTime)

    if (booking.status == BookingStatus.CONFIRMED) {
        when (booking.stayStatus) {
            StayStatus.NONE -> {
                val isEnable = currentTime >= checkInDateTime

                AppButton(
                    onClick = { onAction(StayStatus.CHECK_IN) },
                    enabled = isEnable && !isProcessing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShape.ShapeM,
                    color = PrimaryBlue,
                    text = stringResource(
                        if (isEnable) R.string.check_in_now
                        else R.string.wait_for_check_in_time
                    )
                )
            }

            StayStatus.CHECK_IN -> {
                val isOverdue = currentTime > checkOutDateTime
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.XS)) {
                    if (isOverdue) {
                        Text(
                            text = stringResource(R.string.overdue_checkout_warning),
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    AppButton(
                        onClick = { onAction(StayStatus.CHECK_OUT) },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isOverdue) Color.Red else PrimaryBlue,
                        shape = AppShape.ShapeM,
                        enabled = !isProcessing,
                        text = stringResource(id = R.string.confirm_check_out)
                    )
                }
            }

            StayStatus.CHECK_OUT -> {
                ModernStatusBadge(
                    text = stringResource(id = R.string.stay_completed),
                    color = SuccessGreen,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            StayStatus.NO_SHOW -> {
                ModernStatusBadge(
                    text = stringResource(id = R.string.no_show),
                    color = CancelledRed,
                    icon = Icons.Default.EventBusy,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else if (booking.status == BookingStatus.CANCELLED) {
        AppButton(
            onClick = { onRebookClick() },
            modifier = Modifier.fillMaxWidth(),
            shape = AppShape.ShapeM,
            color = PrimaryBlue,
            text = stringResource(id = R.string.rebook)
        )
    }
}

@Composable
fun ModernStatusBadge(
    text: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(Dimen.PaddingSM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(Dimen.SizeSM)
            )

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Text(
                text = text.uppercase(),
                style = AfacadTypography.labelLarge.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}