package com.example.hotelbooking.features.room.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BlueNavy
import java.time.LocalDate

@Composable
fun CheckAvailabilitySection(
    uiState: BookingUiState,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onCheckClick: () -> Unit
) {
    val isValidDateRange = remember(startDate, endDate) {
        startDate != null && endDate != null && !endDate.isBefore(startDate)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onCheckClick,
            modifier = Modifier.fillMaxWidth().height(Dimen.HeightDefault + 2.dp),
            shape = RoundedCornerShape(AppShape.ShapeM),
            colors = ButtonDefaults.buttonColors(containerColor = BlueNavy),
            enabled = isValidDateRange && uiState !is BookingUiState.Loading
        ) {
            if (uiState is BookingUiState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(Dimen.SizeM)
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                Text(
                    text = stringResource(R.string.checking),
                    color = Color.White
                )
            } else {
                Text(
                    text = stringResource(R.string.check),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        when (uiState) {
            is BookingUiState.Available -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvailableGreen)
                    Spacer(modifier = Modifier.width(AppSpacing.XS))
                    Text(
                        text = stringResource(R.string.rooms_available),
                        fontSize = 15.sp,
                        color = AvailableGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is BookingUiState.SoldOut -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(AppSpacing.XS))
                    Text(
                        uiState.message,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is BookingUiState.Error -> {
                Text(uiState.message, color = Color.Red)
            }
            else -> {}
        }
    }
}