package com.example.hotelbooking.features.hotel.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography

@Composable
fun HotelList(hotels: List<Hotel>, onClick: (String) -> Unit) {

    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            hotels,
            key = { it.id }
        ) { hotelUI ->
            HotelItem(hotelUI, onClick)
        }
    }
}

@Composable
fun HotelItem(
    hotel: Hotel,
    onClick: (String) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(Dimen.HotelCardWidth)
            .height(Dimen.HotelCardHeight)
            .clip(RoundedCornerShape(Dimen.HotelCardCorner))
            .clickable { onClick(hotel.id) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(hotel.thumbnailUrl)
                .crossfade(true)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.3f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingS)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = hotel.name,
                style = JostTypography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingXSPlus))

            Text(
                text = hotel.shortAddress,
                style = JostTypography.bodyLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingXSPlus))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${hotel.pricePerNightMin}/" + stringResource(id = R.string.night),
                    style = JostTypography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )

                Text(
                    text = "⭐${hotel.averageRating}",
                    style = JostTypography.bodyLarge.copy(
                        fontSize = 15.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}