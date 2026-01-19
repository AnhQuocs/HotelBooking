package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.PrimaryBlue

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
fun BookingDetailBottomBar(
    booking: Booking,
    hotel: Hotel,
    isStayProcessing: Boolean,
    onShowBottomSheetClick: () -> Unit,
    onAction: (StayStatus) -> Unit,
    onRebookClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Box(modifier = Modifier.padding(Dimen.PaddingM)) {
            if (booking.status == BookingStatus.PENDING) {
                AppButton(
                    onClick = { onShowBottomSheetClick() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShape.ShapeM,
                    color = PrimaryBlue,
                    text = stringResource(
                        id = R.string.pay_now,
                        booking.totalPrice
                    )
                )
            } else {
                BookingActions(
                    booking = booking,
                    hotel = hotel,
                    isProcessing = isStayProcessing,
                    onAction = { status ->
                        onAction(status)
                    },
                    onRebookClick = { onRebookClick() }
                )
            }
        }
    }
}