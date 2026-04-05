package com.connie.weather.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connie.domain.model.ViewState
import com.connie.domain.model.Weather
import com.connie.domain.repository.WeatherRepository
import com.connie.domain.usecase.GetWeatherForSavedLocationsUseCase
import com.connie.weather.mapper.OpenWeatherResolver
import com.connie.weather.mapper.toDegree
import com.connie.weather.ui.weather.CurrentWeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsWeatherViewModel @Inject constructor(
    private val getWeatherForSavedLocationsUseCase: GetWeatherForSavedLocationsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SavedLocationsWeatherUiState())
    val uiState: StateFlow<SavedLocationsWeatherUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            getWeatherForSavedLocationsUseCase.invoke()
                .map { it.map { weather -> buildWeatherState(weather) } }
                .collect {
                    val weathers = ViewState.Success(it.toPersistentList())
                    _uiState.update { state ->
                        state.copy(weathers = weathers)
                    }
                }
        }
    }

    private fun buildWeatherState(weather: Weather): CityWeatherState {
        return CityWeatherState(
            city = weather.city,
            main = weather.main,
            temperature = weather.temperature.toDegree(),
            minTemperature = weather.minTemperature.toDegree(),
            maxTemperature = weather.maxTemperature.toDegree(),
        )
    }
}