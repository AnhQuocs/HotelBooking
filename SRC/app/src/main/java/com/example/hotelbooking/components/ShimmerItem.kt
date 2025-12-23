package com.example.hotelbooking.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

@Composable
fun ShimmerItem(
    modifier: Modifier = Modifier,
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.9f),
        Color.LightGray.copy(alpha = 0.6f)
    ),
    content: @Composable (brush: Brush) -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")

    val maxDimension = maxOf(size.width, size.height).toFloat()
    val gradientWidth = (maxDimension * 0.3f).coerceAtLeast(200f)

    val startOffset by transition.animateFloat(
        initialValue = -gradientWidth,
        targetValue = size.width.toFloat() + size.height.toFloat() + gradientWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = if (size.width > 0 && size.height > 0) {
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = startOffset, y = startOffset),
            end = Offset(x = startOffset + gradientWidth, y = startOffset + gradientWidth)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                if (coordinates.size.width > 0) {
                    size = coordinates.size
                }
            }
    ) {
        content(shimmerBrush)
    }
}