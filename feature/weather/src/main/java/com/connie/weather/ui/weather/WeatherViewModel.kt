package com.connie.weather.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connie.domain.model.ViewState
import com.connie.domain.model.Weather
import com.connie.domain.repository.WeatherRepository
import com.connie.domain.usecase.GetDailyForecastUseCase
import com.connie.domain.util.TimeHelper
import com.connie.weather.mapper.OpenWeatherResolver
import com.connie.weather.mapper.toDegree
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var fetchDataJob: Job? = null

    // remove me
    private val weather = Weather(
        cityName = "Taipei",
        countryCode = "",
        temperature = 30.0,
        feelsLike = 25.0,
        minTemperature = 21.0,
        maxTemperature = 32.0,
        humidity = 89,
        windSpeed = 10.0,
        main = "Clouds",
        description = "overcast clouds",
        iconCode = "04n",
        timestamp = 1775140122,
        timeZone = 28800,
        sunrise = 1775075221,
        sunset = 1775120556,
    )

    init {
        fetchData()
    }

    private fun fetchData() {
        fetchDataJob?.cancel()
        fetchDataJob = viewModelScope.launch {
            awaitAll(
                async { updateCurrentWeather() },
                async { updateHourlyForecast() },
                async { updateDailyForecast() },
            )
            onDataUpdated()
        }
    }

    private suspend fun updateCurrentWeather() {
        val currentWeatherFlow = weatherRepository.getCurrentWeatherFlow("Tokyo")

        currentWeatherFlow
            .onStart { _uiState.update { it.copy(currentWeather = ViewState.Loading) } }
            .collect { response ->
                if (response == null) {
                    _uiState.update { it.copy(currentWeather = ViewState.Error("No data")) }
                } else {
                    val isDaytime = response.timestamp in response.sunrise..response.sunset
                    _uiState.update {
                        it.copy(
                            isDaytime = isDaytime,
                            currentWeather = ViewState.Success(buildWeatherState(response))
                        )
                    }
                }
            }
    }

    private suspend fun updateHourlyForecast() {
        val hourlyForecastFlow = weatherRepository.getForecastFlow("tokyo")
        hourlyForecastFlow
            .onStart { _uiState.update { it.copy(hourlyForecast = ViewState.Loading) } }
            .collect { response ->
                val hourlyForecastState = if (response == null) {
                    ViewState.Error("No data")
                } else {
                    ViewState.Success(
                        buildHourlyForecastState(response)
                    )
                }
                _uiState.update { it.copy(hourlyForecast = hourlyForecastState) }
            }
    }

    private suspend fun updateDailyForecast() {
        getDailyForecastUseCase.invoke("tokyo")
            .onStart { _uiState.update { it.copy(dailyForecast = ViewState.Loading) } }
            .collect { response ->
                val dailyForecastState = if (response == null) {
                    ViewState.Error("No data")
                } else {
                    ViewState.Success(buildDailyForecastState(response))
                }
                _uiState.update { it.copy(dailyForecast = dailyForecastState) }
            }
    }

    private fun onDataUpdated() {
        val updatedOn = TimeHelper.now().format(TimeHelper.hourMinuteFormatter)
        _uiState.update { it.copy(updatedOn = updatedOn) }
    }

    private fun buildWeatherState(weather: Weather): CurrentWeatherState {
        return CurrentWeatherState(
            cityName = weather.cityName,
            iconUrl = OpenWeatherResolver.resolveIcon(weather.iconCode),
            main = weather.main,
            description = weather.description,
            temperature = weather.temperature.toDegree(),
            feelsLike = weather.feelsLike.toDegree(),
            minTemperature = weather.minTemperature.toDegree(),
            maxTemperature = weather.maxTemperature.toDegree(),
            humidity = weather.humidity.toString(),
            windSpeed = weather.windSpeed.toString(),
        )
    }

    private fun buildHourlyForecastState(
        forecasts: List<Weather>,
    ): PersistentList<ForecastState> {
        return forecasts.map {
            val displayTime = TimeHelper.getZonedDateTime(
                timestampSeconds = it.timestamp,
                timezone = it.timeZone,
            )
                .withMinute(0)
                .format(TimeHelper.hourMinuteFormatter)
            ForecastState(
                displayTime = displayTime,
                weather = buildWeatherState(it),
            )
        }.toPersistentList()
    }

    private fun buildDailyForecastState(
        forecasts: List<Weather>,
    ): PersistentList<ForecastState> {
        return forecasts.map {
            ForecastState(
                displayTime = TimeHelper.format(
                    timestampSeconds = it.timestamp,
                    timezone = it.timeZone,
                    formatter = TimeHelper.dayOfWeekFormatter,
                ),
                weather = buildWeatherState(it),
            )
        }.toPersistentList()
    }
}