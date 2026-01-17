package com.example.hotelbooking.features.booking.presentation.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.AfacadTypography

@Composable
fun CancelledRibbon() {
    Box(
        modifier = Modifier
            .rotate(45f)
            .offset(x = 60.dp)
            .background(CancelledRed)
            .requiredWidth(Dimen.WidthXL)
            .padding(vertical = Dimen.PaddingXXS),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.cancelled).uppercase(),
            style = AfacadTypography.bodySmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}