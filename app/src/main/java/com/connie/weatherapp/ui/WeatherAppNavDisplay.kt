package com.connie.weatherapp.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.connie.weather.ui.weather.WeatherNavKey
import com.connie.weather.ui.weather.WeatherScreen
import kotlinx.coroutines.launch

@Composable
fun WeatherAppNavDisplay() {
    val backStack = remember { mutableStateListOf<NavKey>(WeatherNavKey) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    WeatherModalNavDrawer(
        drawerState = drawerState,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is WeatherNavKey -> NavEntry(key) {
                        WeatherScreen(
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }

                    else -> error("Unknown nav key: $key")
                }
            }
        )
    }
}