package com.example.hotelbooking.features.home.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.hotelbooking.ui.theme.OrangeVibrant
import com.example.hotelbooking.utils.removeAccents

@Composable
fun getHighlightedText(
    text: String,
    query: String,
    highlightColor: Color = OrangeVibrant
): AnnotatedString {
    return buildAnnotatedString {
        val normalizedText = text.lowercase().removeAccents()
        val normalizedQuery = query.lowercase().removeAccents()

        var start = 0
        while (start < text.length) {
            val index = if (normalizedQuery.isNotEmpty()) {
                normalizedText.indexOf(normalizedQuery, start)
            } else -1

            if (index != -1) {
                append(text.substring(start, index))

                withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                    append(text.substring(index, index + query.length))
                }
                start = index + query.length
            } else {
                append(text.substring(start))
                break
            }
        }
    }
}