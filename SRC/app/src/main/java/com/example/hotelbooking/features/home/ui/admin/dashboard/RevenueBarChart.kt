package com.example.hotelbooking.features.home.ui.admin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.HeaderBlue
import java.util.Locale

@Composable
fun RevenueBarChart(data: List<Pair<String, Double>>) {
    val realMax = data.maxOfOrNull { it.second } ?: 0.0
    val displayMax = (if (realMax > 0) realMax * 1.25 else 1.0).toFloat()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (date, amount) ->
                val heightRatio = (amount / displayMax).toFloat().coerceAtLeast(0.01f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (amount > 0) {
                                Text(
                                    text = formatCompactCurrency(amount),
                                    style = AfacadTypography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HeaderBlue
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.XS))
                            }

                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(heightRatio)
                                    .background(
                                        HeaderBlue, RoundedCornerShape(
                                            topStart = AppShape.ShapeXXS, topEnd = AppShape.ShapeXXS
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.S))
                    Text(
                        text = date,
                        style = AfacadTypography.labelSmall,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

private fun formatCompactCurrency(amount: Double): String {
    return when {
        amount >= 1000000 -> String.format(Locale.US, "%.1fM", amount / 1000000)
        amount >= 1000 -> String.format(Locale.US, "%.1fk", amount / 1000)
        else -> amount.toInt().toString()
    }
}