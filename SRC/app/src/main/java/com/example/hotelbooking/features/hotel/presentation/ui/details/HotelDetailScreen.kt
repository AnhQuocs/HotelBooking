package com.example.hotelbooking.features.hotel.presentation.ui.details

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.components.ReadMoreText
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.presentation.ui.ReviewSection
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewState
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.RoomSection
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.IndigoBlue
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun HotelDetailScreen(
    hotel: Hotel,
    roomState: RoomState<List<RoomType>>,
    reviewState: ReviewState<List<Review>>,
    onBackClick: () -> Unit,
    onRoomClick: (String) -> Unit,
    onChatClick: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            HotelDetailTopBar(
                hotel = hotel,
                onBackClick = onBackClick,
                onChatClick = onChatClick
            )
        }, containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(Dimen.PaddingM)
        ) {
            item {
                HotelThumbnail(
                    thumbnailUrl = hotel.thumbnailUrl,
                    averageRating = hotel.averageRating,
                    context = context
                )

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                HotelInfo(
                    name = hotel.name,
                    pricePerNightMin = hotel.pricePerNightMin,
                    address = hotel.address
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            item {
                AmenitySection(amenities = hotel.amenities)
                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            item {
                InfoTitle(text = stringResource(id = R.string.description))

                ReadMoreText(
                    description = hotel.description,
                    maxLine = 3
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))
            }

            item {
                RoomSection(
                    state = roomState,
                    onRoomClick = onRoomClick
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            }

            item {
                ReviewSection(reviewState)
            }
        }
    }
}

@Composable
fun HotelThumbnail(
    thumbnailUrl: String,
    averageRating: Double,
    context: Context
) {
    val padding = Dimen.PaddingS + 2.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingS)
            .height(Dimen.HeightXL4)
            .clip(RoundedCornerShape(AppShape.ShapeL))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(thumbnailUrl)
                .crossfade(true)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .height(40.dp)
                .padding(top = padding, end = padding)
                .clip(RoundedCornerShape(AppShape.ShapeM))
                .background(color = Color.White, RoundedCornerShape(AppShape.ShapeM))
                .align(Alignment.TopEnd),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⭐${averageRating}",
                style = JostTypography.bodyMedium.copy(Color.Black),
                modifier = Modifier.padding(horizontal = padding)
            )
        }
    }
}

@Composable
fun HotelInfo(
    name: String,
    pricePerNightMin: Int,
    address: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = JostTypography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = NearBlack,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$${pricePerNightMin}/",
                style = JostTypography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = PrimaryBlue,
                modifier = Modifier.padding(start = Dimen.PaddingXS)
            )
            Text(stringResource(id = R.string.night), color = Color.Gray, fontSize = 18.sp)
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.S))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = IndigoBlue,
            modifier = Modifier.padding(top = Dimen.PaddingXXS)
        )

        Spacer(modifier = Modifier.width(AppSpacing.XSPlus))

        Text(
            text = address,
            style = JostTypography.labelLarge,
            color = Color.Gray,
            lineHeight = 16.sp
        )
    }
}