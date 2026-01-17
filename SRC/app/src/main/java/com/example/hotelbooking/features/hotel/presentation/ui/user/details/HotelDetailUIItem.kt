package com.example.hotelbooking.features.hotel.presentation.ui.user.details

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.IndigoBlue
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun HotelDetailTopBar(
    hotel: Hotel,
    onBackClick: () -> Unit,
    onChatClick: (String, String, String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
            .height(Dimen.HeightML), contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXL)
                    .clip(RoundedCornerShape(AppShape.ShapeM))
                    .background(color = Color.White, RoundedCornerShape(AppShape.ShapeM))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(AppShape.ShapeM))
                    .clickable { onBackClick() }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeSM),
                    tint = NearBlack
                )
            }

            Text(
                stringResource(id = R.string.detail),
                style = AfacadTypography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = NearBlack,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Box(
                modifier = Modifier
                    .size(Dimen.SizeXL)
                    .clip(RoundedCornerShape(AppShape.ShapeM))
                    .background(color = Color.White, RoundedCornerShape(AppShape.ShapeM))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(AppShape.ShapeM)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_chat),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier
                        .size(Dimen.SizeSM)
                        .clickable {
                            onChatClick(hotel.id, hotel.name, hotel.shortAddress)
                        },
                )
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
                style = AfacadTypography.bodyMedium.copy(Color.Black),
                modifier = Modifier.padding(horizontal = padding)
            )
        }
    }
}

@Composable
fun HotelInfo(
    name: String,
    pricePerNightMin: Int,
    address: String,
    onOpenMap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = AfacadTypography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = NearBlack,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$${pricePerNightMin}/",
                style = AfacadTypography.titleLarge.copy(
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMap() }
            .padding(vertical = Dimen.PaddingXS),
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
            style = AfacadTypography.labelLarge,
            color = Color.Gray,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(Dimen.SizeML),
            tint = IndigoBlue
        )
    }
}