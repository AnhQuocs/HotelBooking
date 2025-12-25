package com.example.hotelbooking.features.hotel.presentation.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.NearBlack

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
                style = JostTypography.bodyLarge.copy(
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