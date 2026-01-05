package com.example.hotelbooking.features.map.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun HotelInfoCard(
    hotel: Hotel,
    onCloseClick: () -> Unit,
    onBookingClick: (String) -> Unit,
    onContactClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(Dimen.PaddingM),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = hotel.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.24f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeS)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    HotelInfo(hotel)
                }

                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black.copy(0.8f),
                    modifier = Modifier
                        .size(Dimen.SizeML)
                        .clickable { onCloseClick() }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            HotelInfoCardButton(
                onBookingClick = { onBookingClick(hotel.id) },
                onContactClick = { onContactClick(hotel.id, hotel.name, hotel.shortAddress) }
            )
        }
    }
}

@Composable
fun HotelInfo(hotel: Hotel) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = hotel.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = JostTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            modifier = Modifier
                .padding(start = Dimen.PaddingXS)
                .fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.Black.copy(alpha = 0.4f)),
                modifier = Modifier.size(Dimen.SizeM)
            )

            Text(
                text = hotel.shortAddress,
                style = JostTypography.labelLarge,
                color = Color.Black.copy(alpha = 0.4f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.titleSmall.toSpanStyle()
                            .copy(color = Color.Blue, fontWeight = FontWeight.Bold)
                    ) {
                        append("$" + hotel.pricePerNightMin.toString())
                    }
                    withStyle(
                        style = MaterialTheme.typography.titleSmall.toSpanStyle()
                            .copy(color = Color.Black)
                    ) {
                        append("/" + stringResource(id = R.string.night))
                    }
                },
                modifier = Modifier.padding(start = Dimen.PaddingXS)
            )

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Text(
                text = "⭐${hotel.averageRating}",
                style = JostTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(start = Dimen.PaddingXS)
            )
        }
    }
}

@Composable
fun HotelInfoCardButton(
    onBookingClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppButton(
            color = PrimaryBlue,
            text = stringResource(id = R.string.booking_now),
            onClick = { onBookingClick() },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(Dimen.HeightDefault)
        )

        IconButton(
            onClick = { onContactClick() },
            modifier = Modifier
                .border(1.dp, Color.LightGray, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_contact),
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM)
            )
        }
    }
}