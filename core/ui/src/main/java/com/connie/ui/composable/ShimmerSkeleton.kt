package com.connie.ui.composable

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.shimmerSkeleton(
    enabled: Boolean = true,
    width: Dp? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    infiniteTransition: InfiniteTransition = rememberShimmerTransition(),
): Modifier = composed {
    if (!enabled) {
        return@composed this
    }
    val widthModifier = if (width != null) {
        Modifier.width(width)
    } else Modifier

    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        start = Offset(translateAnim - 200f, translateAnim),
        end = Offset(translateAnim, translateAnim),
    )

    this
        .then(widthModifier)
        .clip(shape)
        .background(brush)
}

@Composable
fun rememberShimmerTransition(): InfiniteTransition =
    rememberInfiniteTransition(label = "shimmer")