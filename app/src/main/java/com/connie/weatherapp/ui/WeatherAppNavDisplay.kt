package com.connie.weatherapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.connie.weather.ui.weather.WeatherNavKey
import com.connie.weather.ui.weather.WeatherScreen

@Composable
fun WeatherAppNavDisplay() {
    val backStack = remember { mutableStateListOf<NavKey>(WeatherNavKey) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is WeatherNavKey -> NavEntry(key) {
                    WeatherScreen()
                }

                else -> {
                    error("Unknown nav key: $key")
                }
            }
        }
    )
}