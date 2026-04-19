package com.connie.weather.ui.weather

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import com.connie.ui.composable.VerticalFadingEdges
import com.connie.ui.composable.rememberFadeVisibility
import com.connie.ui.composable.rememberShimmerTransition
import com.connie.weather.R
import com.connie.weather.ui.weather.composable.HourlyForecast
import com.connie.weather.ui.weather.composable.CurrentWeatherInfo
import com.connie.weather.ui.weather.composable.WeatherFooter
import com.connie.weather.ui.weather.composable.dailyForecastItems
import com.connie.weather.ui.composable.WeatherTopBar
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

@Serializable
data class WeatherNavKey(val city: City? = null) : NavKey

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    onOpenDrawer: () -> Unit,
) {
    val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val limitReachedMessage = stringResource(R.string.weather__you_can_only_save)

    LaunchedEffect(Unit) {
        weatherViewModel.effect.collect {
            when (it) {
                is WeatherUiEffect.SavedLocationsLimitReached -> {
                    snackbarHostState.showSnackbar(limitReachedMessage)
                }
            }
        }
    }
    WeatherScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onOpenDrawer = onOpenDrawer,
        onUiEvent = weatherViewModel::onUiEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherScreenContent(
    uiState: WeatherUiState,
    snackbarHostState: SnackbarHostState,
    onOpenDrawer: () -> Unit = {},
    onUiEvent: (WeatherUiEvent) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val colorScheme = remember(uiState.isDaytime) {
        if (uiState.isDaytime) lightColorScheme() else darkColorScheme()
    }
    val shimmerTransition = rememberShimmerTransition()

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                WeatherTopBar(
                    scrollBehavior = scrollBehavior,
                    uiState = uiState,
                    onOpenDrawer = onOpenDrawer,
                    onUiEvent = onUiEvent,
                )
            }
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = {
                    onUiEvent(WeatherUiEvent.Refresh)
                },
                modifier = Modifier.fillMaxSize(),
            ) {
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
                        dailyForecastItems(
                            viewState = uiState.dailyForecast,
                            shimmerTransition = shimmerTransition,
                        )

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
        uiState = WeatherUiState(
            currentWeather = ViewState.Success(currentWeatherState),
            hourlyForecast = ViewState.Success(hourly),
            dailyForecast = ViewState.Success(daily),
            updatedOn = "00:00"
        ),
        snackbarHostState = SnackbarHostState(),
    )
}