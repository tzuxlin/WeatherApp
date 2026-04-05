package com.connie.weather.ui.composable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.connie.ui.R
import com.connie.weather.ui.weather.WeatherUiEvent
import com.connie.weather.ui.weather.WeatherUiState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WeatherTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    uiState: WeatherUiState,
    onOpenDrawer: () -> Unit,
    onUiEvent: (WeatherUiEvent) -> Unit,
) {
    TopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
        actions = {
            IconButton(
                onClick = { onUiEvent(WeatherUiEvent.ToggleSaved) },
                enabled = uiState.isSavedEnabled,
            ) {
                val resId =
                    if (uiState.isSaved) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_boarder
                Icon(
                    painter = painterResource(resId),
                    contentDescription = "save"
                )
            }
            IconButton(
                onClick = onOpenDrawer,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = "menu"
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}