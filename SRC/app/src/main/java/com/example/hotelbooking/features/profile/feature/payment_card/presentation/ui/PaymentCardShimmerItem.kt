package com.example.hotelbooking.features.profile.feature.payment_card.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.components.ShimmerItem
import com.example.hotelbooking.ui.dimens.AppShape

@Composable
fun PaymentCardShimmerItem() {
    ShimmerItem(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
    ) { brush ->
        Card(
            modifier = Modifier.border(0.1.dp, color = Color.LightGray, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp, 32.dp)
                        .background(brush, RoundedCornerShape(6.dp))
                        .align(Alignment.TopStart)
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(brush, CircleShape)
                        .align(Alignment.TopEnd)
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(24.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Box(modifier = Modifier.size(60.dp, 10.dp).background(brush, RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.size(120.dp, 18.dp).background(brush, RoundedCornerShape(4.dp)))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(modifier = Modifier.size(40.dp, 10.dp).background(brush, RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.size(60.dp, 18.dp).background(brush, RoundedCornerShape(4.dp)))
                        }
                    }
                }
            }
        }
    }
}