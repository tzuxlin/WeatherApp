package com.connie.weather.ui.weather

sealed interface WeatherUiEvent {
    data object ToggleSaved: WeatherUiEvent
}

sealed interface WeatherUiEffect {
    data object SavedLocationsLimitReached: WeatherUiEffect
}