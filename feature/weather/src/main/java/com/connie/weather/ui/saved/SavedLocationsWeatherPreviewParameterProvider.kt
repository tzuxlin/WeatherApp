package com.connie.weather.ui.saved

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import kotlinx.collections.immutable.persistentListOf

class SavedLocationsWeatherPreviewParameterProvider :
    PreviewParameterProvider<SavedLocationsWeatherUiState> {
    override val values: Sequence<SavedLocationsWeatherUiState> = sequenceOf(
        SavedLocationsWeatherUiState(
            weathers = ViewState.Success(
                persistentListOf(
                    CityWeatherState(
                        city = City("Taipei", "", "", ""),
                        temperature = "28°",
                        maxTemperature = "30°",
                        minTemperature = "22°",
                        main = "Cloudy"
                    ),
                    CityWeatherState(
                        city = City("Tokyo", "", "", ""),
                        temperature = "16°",
                        maxTemperature = "19°",
                        minTemperature = "5°",
                        main = "Rainy"
                    )
                )
            )
        ),
        SavedLocationsWeatherUiState(weathers = ViewState.Loading),
        SavedLocationsWeatherUiState(weathers = ViewState.Error("Unable to get the data, please try again later.")),
    )
}