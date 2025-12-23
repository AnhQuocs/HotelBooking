package com.example.hotelbooking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R

@Composable
fun AppTitle(
    modifier: Modifier = Modifier,
    text1: String,
    text2: String,
    onClick: () -> Unit,
    fontSize: Int = 18
) {
    val jostFont = FontFamily(Font(R.font.jost_font))

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text1,
            fontSize = fontSize.sp,
            fontFamily = jostFont,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Text(
            text = text2,
            fontSize = 16.sp,
            fontFamily = jostFont,
            color = Color(0xFF0A3A7A),
            modifier = Modifier.clickable { onClick() }
        )
    }
}