package com.example.hotelbooking.features.auth.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun AuthTitle(
    title: String,
    desc: String
) {
    Text(
        text = title,
        color = Color.Black,
        style = JostTypography.headlineMedium.copy(fontWeight = FontWeight.Bold)
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingS))

    Text(
        text = desc,
        color = TextTertiary,
        textAlign = TextAlign.Center,
        style = JostTypography.labelLarge.copy(fontSize = 16.sp),
        modifier = Modifier.fillMaxWidth()
    )
}