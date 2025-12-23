package com.example.hotelbooking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.ui.dimens.AppShape

@Composable
fun AppTitleShimmer(
    modifier: Modifier = Modifier,
) {
    ShimmerItem(modifier = modifier) { brush ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .height(23.dp)
                    .width(120.dp)
                    .background(brush, RoundedCornerShape(AppShape.ShapeXXS))
            )

            Box(
                modifier = Modifier
                    .height(16.dp)
                    .width(60.dp)
                    .background(brush, RoundedCornerShape(AppShape.ShapeXXS))
            )
        }
    }
}