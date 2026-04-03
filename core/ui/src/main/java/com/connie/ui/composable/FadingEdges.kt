package com.connie.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class FadeVisibility(
    val showLeadingFade: Boolean,
    val showTrailingFade: Boolean,
)

@Composable
fun HorizontalFadingEdges(
    fadeVisibility: FadeVisibility,
    modifier: Modifier = Modifier,
    fadeWidth: Dp = 36.dp,
    fadeColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier) {
        content()

        if (fadeVisibility.showLeadingFade) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .width(fadeWidth)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                fadeColor,
                                Color.Transparent,
                            )
                        )
                    )
            )
        }

        if (fadeVisibility.showTrailingFade) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(fadeWidth)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                fadeColor,
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun VerticalFadingEdges(
    fadeVisibility: FadeVisibility,
    modifier: Modifier = Modifier,
    fadeHeight: Dp = 36.dp,
    fadeColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier) {
        content()

        if (fadeVisibility.showLeadingFade) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                fadeColor,
                                Color.Transparent,
                            )
                        )
                    )
            )
        }

        if (fadeVisibility.showTrailingFade) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                fadeColor,
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun rememberFadeVisibility(
    listState: LazyListState,
): FadeVisibility {
    val showLeadingFade by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > 0
        }
    }

    val showTrailingFade by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItemsCount = layoutInfo.totalItemsCount

            if (lastVisibleItem == null || totalItemsCount == 0) {
                false
            } else {
                lastVisibleItem.index < totalItemsCount - 1 ||
                        lastVisibleItem.offset + lastVisibleItem.size > layoutInfo.viewportEndOffset
            }
        }
    }

    return FadeVisibility(showLeadingFade, showTrailingFade)
}