package com.connie.weather.ui.weather

import com.connie.domain.model.ViewState
import kotlinx.collections.immutable.PersistentList

data class WeatherUiState(
    val isDaytime: Boolean = false,
    val currentWeather: ViewState<CurrentWeatherState> = ViewState.Loading,
    val hourlyForecast: ViewState<PersistentList<ForecastState>> = ViewState.Loading,
    val dailyForecast: ViewState<PersistentList<ForecastState>> = ViewState.Loading,
    val updatedOn: String = "",
)

data class CurrentWeatherState(
    val cityName: String = "",
    val iconUrl: String = "",
    val main: String = "",
    val description: String = "",
    val temperature: String = "",
    val feelsLike: String = "",
    val minTemperature: String = "",
    val maxTemperature: String = "",
    val humidity: String = "",
    val windSpeed: String = "",
)

data class ForecastState(
    val displayTime: String = "",
    val weather: CurrentWeatherState = CurrentWeatherState(),
)