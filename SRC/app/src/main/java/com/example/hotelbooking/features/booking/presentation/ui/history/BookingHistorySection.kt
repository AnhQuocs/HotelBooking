package com.example.hotelbooking.features.booking.presentation.ui.history

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.presentation.ui.checkout.CheckoutSummaryItem
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingWithHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SlateGray
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toDate().toInstant(), ZoneId.systemDefault())
}

@Composable
fun BookingHistorySection(
    state: BookingHistoryState<List<BookingWithHotel>>,
    onDetailClick: (String, String) -> Unit
) {
    val context = LocalContext.current

    when(state) {
        is BookingHistoryState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is BookingHistoryState.Success -> {
            val items = state.data

            if (items.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_bookings_yet),
                    style = JostTypography.bodyLarge.copy(
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge),
                    contentPadding = PaddingValues(bottom = Dimen.PaddingM)
                ) {
                    items(
                        items = items,
                        key = { it.booking.bookingId }
                    ) { item ->
                        item.hotel?.let {
                            BookingHistoryCard(
                                booking = item.booking,
                                hotel = it,
                                context = context,
                                onDetailClick = {
                                    onDetailClick(
                                        item.booking.bookingId,
                                        item.booking.roomTypeId
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        is BookingHistoryState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun BookingHistoryCard(
    booking: Booking,
    hotel: Hotel,
    context: Context,
    onDetailClick: () -> Unit
) {
    val start = booking.startDate.toLocalDateTime()
    val end = booking.endDate.toLocalDateTime()

    val monthStr = start.month.name
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    val dateStr = "${start.dayOfMonth}-${end.dayOfMonth} $monthStr ${start.year}"

    val nights = (booking.endDate.seconds - booking.startDate.seconds) / (60 * 60 * 24)

    val numberOfGuest = booking.numberOfGuests
    val guestText =
        if (numberOfGuest == 1)
            stringResource(id = R.string.guest)
        else
            stringResource(id = R.string.guests)

    val dateText =
        if (nights == 1L)
            stringResource(id = R.string.date)
        else
            stringResource(id = R.string.dates)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppShape.ShapeL))
            .border(0.5.dp, color = Color.LightGray, RoundedCornerShape(AppShape.ShapeL))
            .background(color = Color.White, RoundedCornerShape(AppShape.ShapeL))
            .clickable { onDetailClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingSM)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(hotel.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppShape.ShapeL))
                    .width(90.dp)
                    .height(165.dp)
            )

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = hotel.name,
                        style = JostTypography.titleMedium.copy(
                            color = Color.Black,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Dimen.PaddingXXS)
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    Text(
                        text = "⭐${hotel.averageRating}",
                        style = JostTypography.bodyLarge.copy(
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.XS))
                    Text(
                        text = hotel.shortAddress,
                        style = JostTypography.bodyLarge.copy(
                            fontSize = 15.sp,
                            color = SlateGray,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${hotel.pricePerNightMin}",
                        style = JostTypography.bodyLarge.copy(
                            color = BlueNavy,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(start = Dimen.PaddingXS)
                    )
                    Text("/" + stringResource(id = R.string.night), color = Color.Black, fontSize = 14.sp)
                }

                LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingSM))

                CheckoutSummaryItem(
                    icon = R.drawable.ic_calendar,
                    key = dateText,
                    value = dateStr
                )

                CheckoutSummaryItem(
                    icon = R.drawable.ic_people,
                    key = guestText,
                    value = "$numberOfGuest $guestText " + stringResource(id = R.string.one_room)
                )
            }
        }
    }
}