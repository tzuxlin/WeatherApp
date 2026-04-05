package com.connie.weather.ui.saved

import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import kotlinx.collections.immutable.PersistentList

data class SavedLocationsWeatherUiState(
    val weathers: ViewState<PersistentList<CityWeatherState>> = ViewState.Loading,
)

data class CityWeatherState(
    val city: City,
    val main: String = "",
    val temperature: String = "",
    val minTemperature: String = "",
    val maxTemperature: String = "",
)