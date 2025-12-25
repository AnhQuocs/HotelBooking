package com.example.hotelbooking.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R

@Composable
fun InfoTitle(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Int = 18
) {
    val jostFont = FontFamily(Font(R.font.jost_font))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = jostFont,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}