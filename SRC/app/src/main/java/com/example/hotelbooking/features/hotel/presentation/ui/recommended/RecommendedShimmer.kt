package com.example.hotelbooking.features.hotel.presentation.ui.recommended

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.components.ShimmerItem
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun RecommendedShimmer() {
    val rowHeight = Dimen.HeightML + 7.dp

    ShimmerItem(
        modifier = Modifier
            .fillMaxWidth()
    ) { brush ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = Dimen.PaddingM)
            ) {
                repeat(3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight)
                            .padding(Dimen.PaddingS)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Dimen.SizeXXLPlus)
                                .background(brush, RoundedCornerShape(AppShape.ShapeM))
                        )

                        Spacer(modifier = Modifier.width(AppSpacing.M))

                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(20.dp)
                                    .fillMaxWidth(0.65f)
                                    .background(brush, RoundedCornerShape(AppShape.ShapeXXS))
                            )

                            Spacer(modifier = Modifier.height(AppSpacing.S))

                            Box(
                                modifier = Modifier
                                    .height(18.dp)
                                    .fillMaxWidth(0.5f)
                                    .background(brush, RoundedCornerShape(AppShape.ShapeXXS))
                            )

                            Spacer(modifier = Modifier.height(AppSpacing.S))

                            Box(
                                modifier = Modifier
                                    .height(20.dp)
                                    .fillMaxWidth(0.35f)
                                    .background(brush, RoundedCornerShape(AppShape.ShapeXXS))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.XS))

                    LineGray()
                }
            }
        }
    }
}