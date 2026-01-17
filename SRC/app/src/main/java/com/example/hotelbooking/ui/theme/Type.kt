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

val AfacadTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 60.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Bold,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontSize = 48.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Bold
    ),

    headlineLarge = TextStyle(
        fontSize = 34.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontSize = 30.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.SemiBold
    ),

    titleLarge = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.SemiBold,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Medium
    ),

    bodyLarge = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Normal,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp
    ),

    labelLarge = TextStyle(
        fontSize = 15.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontSize = 13.sp,
        fontFamily = FontFamily(Font(R.font.font_afacad_variable)),
        fontWeight = FontWeight.Medium
    )
)