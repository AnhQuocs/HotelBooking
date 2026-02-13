package com.example.hotelbooking.features.hotel.presentation.ui.user

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.user.details.AmenityProvider
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RatingYellow
import com.example.hotelbooking.ui.theme.SlateGray

@Composable
fun AllHotelItem(hotel: Hotel, context: Context, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingS)
            .clickable { onClick(hotel.id) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(AppShape.ShapeM))
                .background(color = Color.Black.copy(alpha = 0.3f))
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

            HotelRatingBlurBadge(
                modifier = Modifier.align(Alignment.TopStart),
                rating = "%.1f".format(hotel.averageRating)
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = hotel.name,
                style = AfacadTypography.titleMedium.copy(fontSize = 20.sp, color = Color.Black),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "$${hotel.pricePerNightMin}+",
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                style = AfacadTypography.titleMedium.copy(fontSize = 20.sp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = hotel.shortAddress,
                style = AfacadTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = SlateGray
            )

            Text(
                text = stringResource(id = R.string.per_night),
                color = SlateGray,
                style = AfacadTypography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.S))

        AmenitySummary(hotel.amenities)
    }
}

@Composable
fun HotelRatingBlurBadge(
    rating: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = Dimen.PaddingS, start = Dimen.PaddingS)
            .clip(RoundedCornerShape(50))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = RenderEffect
                            .createBlurEffect(
                                20f,
                                20f,
                                Shader.TileMode.CLAMP
                            )
                            .asComposeRenderEffect()
                    }
                }
                .background(Color.White.copy(alpha = 0.5f))
        )

        Row(
            modifier = Modifier
                .padding(horizontal = Dimen.PaddingS, vertical = Dimen.PaddingXS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.XS)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = RatingYellow,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = rating,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AmenitySummary(amenities: List<String>) {
    val displayList = remember(amenities) {
        amenities
            .mapNotNull { title -> AmenityProvider.find(title) }
            .take(2)
    }

    if (displayList.isEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        displayList.forEachIndexed { index, amenity ->
            Row {
                Icon(
                    painter = painterResource(id = amenity.iconRes),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(Dimen.SizeSM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.XS))

                Text(
                    text = stringResource(id = amenity.titleRes),
                    color = Color.Black,
                    style = AfacadTypography.bodyMedium
                )
            }

            if (index != displayList.lastIndex) {
                Text(
                    text = " • ",
                    color = Color.Black,
                    style = AfacadTypography.bodyMedium
                )
            }
        }
    }
}