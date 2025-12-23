package com.example.hotelbooking.features.hotel.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.components.ShimmerItem
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun HotelItemShimmer() {
    val rowWidth = Dimen.HotelCardWidth
    val rowHeight = Dimen.HotelCardHeight

    ShimmerItem(
        modifier = Modifier
            .width(rowWidth)
            .height(rowHeight)
            .clip(RoundedCornerShape(Dimen.HotelCardCorner))
    ) { brush ->
        Box(
            modifier = Modifier
                .width(Dimen.HotelCardWidth)
                .height(Dimen.HotelCardHeight)
                .clip(RoundedCornerShape(Dimen.HotelCardCorner))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingS)
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.85f)
                        .background(brush, RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(Dimen.PaddingXSPlus))

                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.6f)
                        .background(brush, RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(Dimen.PaddingXSPlus))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(60.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(30.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}