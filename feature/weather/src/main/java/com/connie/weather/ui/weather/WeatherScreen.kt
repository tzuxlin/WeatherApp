package com.connie.weather.ui.weather

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.connie.domain.model.ViewState
import com.connie.ui.composable.VerticalFadingEdges
import com.connie.ui.composable.rememberFadeVisibility
import com.connie.weather.ui.weather.composable.HourlyForecast
import com.connie.weather.ui.weather.composable.CurrentWeatherInfo
import com.connie.weather.ui.weather.composable.WeatherFooter
import com.connie.weather.ui.weather.composable.dailyForecastItems
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

@Serializable
data object WeatherNavKey : NavKey

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
) {
    val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    WeatherScreenContent(uiState)
}

@Composable
private fun WeatherScreenContent(uiState: WeatherUiState) {
    val lazyListState = rememberLazyListState()

    MaterialTheme(colorScheme = if (uiState.isDaytime) lightColorScheme() else darkColorScheme()) {
        Scaffold { paddingValues ->
            VerticalFadingEdges(
                fadeVisibility = rememberFadeVisibility(lazyListState),
                fadeHeight = 48.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                LazyColumn(
                    state = lazyListState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = paddingValues,
                ) {
                    item {
                        CurrentWeatherInfo(weatherViewState = uiState.currentWeather)
                    }
                    item {
                        HourlyForecast(uiState.hourlyForecast)
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                    dailyForecastItems(uiState.dailyForecast)

                    if (uiState.updatedOn.isNotEmpty()) {
                        item {
                            WeatherFooter(uiState.updatedOn)
                        }
                    }
                }
            }

        }
    }
}

@Preview(widthDp = 480)
@Composable
private fun PreviewWeatherScreenContent() {
    val currentWeatherState = CurrentWeatherState(
        cityName = "Taipei",
        main = "Sunny",
        description = "overcast clouds",
        temperature = "30\u00B0",
        maxTemperature = "32\u00B0",
        minTemperature = "25\u00B0",
        feelsLike = "28\u00B0",
    )
    val hourly = buildList {
        repeat(10) {
            add(
                ForecastState(
                    displayTime = "24:00",
                    weather = currentWeatherState,
                )
            )
        }
    }.toPersistentList()

    val daily = buildList {
        repeat(5) {
            add(
                ForecastState(
                    displayTime = "Monday",
                    weather = currentWeatherState,
                )
            )
        }
    }.toPersistentList()

    WeatherScreenContent(
        WeatherUiState(
            currentWeather = ViewState.Success(currentWeatherState),
            hourlyForecast = ViewState.Success(hourly),
            dailyForecast = ViewState.Success(daily),
            updatedOn = "00:00"
        )
    )
}