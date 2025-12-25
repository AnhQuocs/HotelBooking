package com.example.hotelbooking.features.booking.presentation.ui.checkout

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.TextPrimaryDark
import com.example.hotelbooking.ui.theme.WarningOrange
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    totalTime: Int = 10 * 60,
    onTimeout: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(totalTime) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onTimeout()
    }

    Text(
        text = stringResource(
            R.string.payment_countdown,
            formatTime(timeLeft)
        ),
        fontSize = 14.sp,
        color = WarningOrange,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}

@Composable
fun HotelInfo(hotel: Hotel, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightML),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(hotel.thumbnailUrl)
                .crossfade(true)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimen.SizeMega)
                .clip(RoundedCornerShape(AppShape.ShapeM))
        )

        Spacer(modifier = Modifier.width(AppSpacing.S))

        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = hotel.name,
                color = Color.Black,
                style = JostTypography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.padding(start = Dimen.PaddingXS)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier.size(Dimen.SizeSM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.XS))

                Text(
                    text = hotel.shortAddress,
                    style = JostTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${hotel.pricePerNightMin}",
                    style = JostTypography.bodyLarge.copy(
                        color = BlueNavy,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = Dimen.PaddingXS)
                )
                Text(
                    "/" + stringResource(id = R.string.night),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "⭐${hotel.averageRating}",
            style = JostTypography.labelLarge.copy(fontSize = 15.sp),
            color = Color.Black
        )
    }
}

@Composable
fun CheckoutSummaryCard(
    date: String,
    numberOfGuest: Int,
    guestName: String,
    roomName: String,
    phone: String,
    totalPrice: String
) {
    val guestText = stringResource(
        if (numberOfGuest == 1) R.string.guest else R.string.guests
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingSM)
        ) {
            Text(
                text = stringResource(R.string.your_booking),
                style = JostTypography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            )

            CheckoutSummaryItem(
                icon = R.drawable.ic_calendar,
                key = stringResource(R.string.dates),
                value = date
            )

            CheckoutSummaryItem(
                icon = R.drawable.ic_people,
                key = stringResource(R.string.guest),
                value = stringResource(
                    R.string.guest_room_format,
                    numberOfGuest,
                    guestText
                )
            )

            CheckoutSummaryItem(
                icon = R.drawable.ic_user,
                key = stringResource(R.string.name),
                value = guestName
            )

            CheckoutSummaryItem(
                icon = R.drawable.ic_building,
                key = stringResource(R.string.room_type),
                value = roomName
            )

            CheckoutSummaryItem(
                icon = R.drawable.ic_call,
                key = stringResource(R.string.phone),
                value = phone
            )

            DashedLine(modifier = Modifier.padding(vertical = Dimen.PaddingM))

            Text(
                text = stringResource(R.string.price_details),
                style = JostTypography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimen.PaddingXSPlus),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.price),
                    style = JostTypography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimaryDark
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$$totalPrice",
                    style = JostTypography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimaryDark
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.discount),
                    style = JostTypography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimaryDark
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "-$0.0",
                    style = JostTypography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimaryDark
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.total_price),
                    style = JostTypography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$$totalPrice",
                    style = JostTypography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                )
            }
        }
    }
}

@Composable
fun CheckoutSummaryItem(
    icon: Int,
    key: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingXSPlus),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color(0xFF78828A)),
            modifier = Modifier.size(Dimen.SizeSM)
        )

        Spacer(modifier = Modifier.width(AppSpacing.XSPlus))

        Text(
            text = key,
            style = JostTypography.labelLarge.copy(
                fontSize = 15.sp,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Normal
            ),
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            style = JostTypography.labelLarge.copy(
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun DashedLine(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 10.dp,
    gapLength: Dp = 5.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
    ) {
        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f
        )
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth.toPx(),
            pathEffect = pathEffect
        )
    }
}