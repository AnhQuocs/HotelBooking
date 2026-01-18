package com.example.hotelbooking.features.booking.presentation.ui.rebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun RebookBottomBar(
    pricePerNight: Int,
    startDate: LocalDate?,
    endDate: LocalDate?,
    uiState: BookingUiState,
    onBookClick: () -> Unit,
    onTotalPriceChange: (Long) -> Unit
) {
    val nights = remember(startDate, endDate) {
        if (startDate != null && endDate != null && endDate.isAfter(startDate)) {
            ChronoUnit.DAYS.between(startDate, endDate)
        } else {
            1L
        }
    }

    val totalPrice = pricePerNight * nights
    val isAvailable = uiState is BookingUiState.Available

    LaunchedEffect(totalPrice) {
        onTotalPriceChange(totalPrice)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Price",
                    style = AfacadTypography.bodyMedium.copy(color = Color.Gray)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${totalPrice.toInt()}",
                        style = AfacadTypography.titleLarge.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "/$nights night${if (nights > 1) "s" else ""}",
                        style = AfacadTypography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                }
            }

            Button(
                onClick = onBookClick,
                enabled = isAvailable,
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueNavy,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(
                    text = stringResource(R.string.book_now),
                    style = AfacadTypography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}