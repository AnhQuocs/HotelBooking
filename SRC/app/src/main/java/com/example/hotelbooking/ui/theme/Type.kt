package com.example.hotelbooking.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val JostTypography = Typography(

    displayLarge = TextStyle(
        fontSize = 57.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Bold
    ),
    displayMedium = TextStyle(
        fontSize = 45.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Bold
    ),
    displaySmall = TextStyle(
        fontSize = 36.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.SemiBold
    ),

    headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.SemiBold
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Medium
    ),

    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Medium
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Medium
    ),

    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Normal
    ),

    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.jost_font)),
        fontWeight = FontWeight.Medium
    ),
)