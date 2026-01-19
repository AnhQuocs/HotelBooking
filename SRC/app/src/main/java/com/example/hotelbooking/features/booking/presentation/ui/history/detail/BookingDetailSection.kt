package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.presentation.ui.checkout.CheckoutSummaryItem
import com.example.hotelbooking.features.booking.presentation.ui.checkout.CountdownTimer
import com.example.hotelbooking.features.booking.presentation.ui.checkout.DashedLine
import com.example.hotelbooking.features.booking.presentation.ui.checkout.HotelInfo
import com.example.hotelbooking.features.booking.presentation.ui.history.toLocalDateTime
import com.example.hotelbooking.features.booking.presentation.ui.util.BookingQRCodeFull
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SuccessGreen
import com.example.hotelbooking.ui.theme.TextTertiary
import java.util.Calendar

@Composable
fun BookingDetailItem(
    hotel: Hotel?,
    booking: Booking,
    roomDetailState: RoomState<RoomType>,
    seconds: Int,
    onTimeOut: () -> Unit
) {
    val context = LocalContext.current

    val start = booking.startDate.toLocalDateTime()
    val end = booking.endDate.toLocalDateTime()

    val monthStr = start.month.name
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    val dateStr = "${start.dayOfMonth}-${end.dayOfMonth} $monthStr ${start.year}"

    val guest = booking.guest

    val guestText =
        if (booking.numberOfGuests == 1)
            stringResource(id = R.string.guest)
        else
            stringResource(id = R.string.guest)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeM),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.PaddingXSPlus)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingSM)
        ) {
            if (booking.status == BookingStatus.PENDING) {
                CountdownTimer(
                    totalTime = seconds,
                    onTimeout = { onTimeOut() }
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            Text(
                stringResource(id = R.string.your_hotel),
                style = AfacadTypography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = TextTertiary
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.XS))

            if (hotel != null) {
                HotelInfo(hotel, context)
            } else {
                Text(
                    text = stringResource(id = R.string.hotel_load_error),
                    style = AfacadTypography.bodyLarge.copy(
                        fontSize = 15.sp,
                        color = Color.Red
                    )
                )
            }

            DashedLine(modifier = Modifier.padding(vertical = Dimen.PaddingM))

            Text(
                stringResource(id = R.string.your_room),
                style = AfacadTypography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = TextTertiary
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.XS))

            when (roomDetailState) {
                is RoomState.Loading -> {
                    Box(
                        modifier = Modifier
                            .height(Dimen.HeightXL2)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is RoomState.Success<*> -> {
                    RoomImage(
                        room = (roomDetailState as RoomState.Success<RoomType>).data,
                        context = context
                    )
                }

                is RoomState.Error -> {
                    Text(
                        text = stringResource(
                            id = R.string.error,
                            roomDetailState.message
                        )
                    )
                }
            }

            DashedLine(modifier = Modifier.padding(vertical = Dimen.PaddingM))

            Text(
                stringResource(id = R.string.your_booking),
                style = AfacadTypography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = TextTertiary
                )
            )

            CheckoutSummaryItem(
                icon = R.drawable.ic_calendar,
                key = stringResource(id = R.string.dates),
                value = dateStr
            )
            CheckoutSummaryItem(
                icon = R.drawable.ic_people,
                key = stringResource(id = R.string.guests),
                value = "${booking.numberOfGuests} $guestText " + stringResource(id = R.string.one_room)
            )
            CheckoutSummaryItem(
                icon = R.drawable.ic_user,
                key = stringResource(id = R.string.name),
                value = guest.name
            )
            if (roomDetailState is RoomState.Success) {
                val room = roomDetailState.data
                CheckoutSummaryItem(
                    icon = R.drawable.ic_building,
                    key = stringResource(id = R.string.room_type),
                    value = room.name
                )
            }
            CheckoutSummaryItem(
                icon = R.drawable.ic_call,
                key = stringResource(id = R.string.phone),
                value = guest.phone
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            BookingQRCodeFull(
                bookingWithHotel = BookingWithHotel(booking, hotel),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RoomImage(room: RoomType, context: Context) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(room.imageUrl)
            .crossfade(true)
            .crossfade(200)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(Dimen.HeightXL3)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppShape.ShapeM))
    )
}