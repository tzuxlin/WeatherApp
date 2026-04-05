package com.connie.weather.ui.weather

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import com.connie.domain.model.Weather
import com.connie.domain.repository.CityRepository
import com.connie.domain.repository.WeatherRepository
import com.connie.domain.usecase.GetDailyForecastUseCase
import com.connie.domain.usecase.GetDefaultCityUseCase
import com.connie.domain.usecase.ToggleSavedCityUseCase
import com.connie.domain.util.TimeHelper
import com.connie.weather.mapper.OpenWeatherResolver
import com.connie.weather.mapper.toDegree
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = WeatherViewModel.Factory::class)
class WeatherViewModel @AssistedInject constructor(
    @Assisted private val weatherNavKey: WeatherNavKey,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val getDefaultCityUseCase: GetDefaultCityUseCase,
    private val toggleSavedCityUseCase: ToggleSavedCityUseCase,
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<WeatherUiEffect>()
    val effect: SharedFlow<WeatherUiEffect> = _effect.asSharedFlow()

    private var fetchDataJob: Job? = null
    private lateinit var city: City

    init {
        fetchData()
    }

    fun onUiEvent(event: WeatherUiEvent) {
        when (event) {
            WeatherUiEvent.ToggleSaved -> {
                viewModelScope.launch {
                    val canToggle = toggleSavedCityUseCase.invoke(city)
                    if (!canToggle) {
                        _effect.emit(WeatherUiEffect.SavedLocationsLimitReached)
                    }
                }
            }
        }
    }

    private fun fetchData() {
        fetchDataJob?.cancel()
        fetchDataJob = viewModelScope.launch {
            Log.d("Connie", "weather vm key ${weatherNavKey.city}")
            city = weatherNavKey.city ?: getDefaultCityUseCase.invoke() ?: run {
                updateErrorState()
                return@launch
            }
            Log.d("Connie", "weather vm fetch $city")
            updateCurrentWeatherSavedStatus()
            awaitAll(
                async { updateCurrentWeather() },
                async { updateHourlyForecast() },
                async { updateDailyForecast() },
            )
            onDataUpdated()
        }
    }

    private fun updateCurrentWeatherSavedStatus() {
        viewModelScope.launch {
            cityRepository.getIsCitySavedFlow(city)
                .collectLatest { isSaved ->
                    Log.d("Connie", "weather vm isSaved $isSaved, $city")
                    _uiState.update {
                        it.copy(
                            isSaved = isSaved,
                            isSavedEnabled = true,
                        )
                    }
                }
        }
    }

    private suspend fun updateCurrentWeather() {
        weatherRepository.getCurrentWeatherFlow(city)
            .onStart { _uiState.update { it.copy(currentWeather = ViewState.Loading) } }
            .collect { response ->
                if (response == null) {
                    updateErrorState()
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
        weatherRepository.getForecastFlow(lat = city.lat, lon = city.lon)
            .onStart { _uiState.update { it.copy(hourlyForecast = ViewState.Loading) } }
            .collect { response ->
                val hourlyForecastState = if (response == null) {
                    ViewState.Error()
                } else {
                    ViewState.Success(
                        buildHourlyForecastState(response)
                    )
                }
                _uiState.update { it.copy(hourlyForecast = hourlyForecastState) }
            }
    }

    private suspend fun updateDailyForecast() {
        getDailyForecastUseCase.invoke(city)
            .onStart { _uiState.update { it.copy(dailyForecast = ViewState.Loading) } }
            .collect { response ->
                val dailyForecastState = if (response == null) {
                    ViewState.Error()
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

    private fun updateErrorState() {
        _uiState.update {
            it.copy(
                currentWeather = ViewState.Error(),
                hourlyForecast = ViewState.Error(),
                dailyForecast = ViewState.Error(),
                updatedOn = "",
                isSaved = false,
                isSavedEnabled = false,
            )
        }
    }

    private fun buildWeatherState(weather: Weather): CurrentWeatherState {
        return CurrentWeatherState(
            cityName = weather.city.name,
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

    @AssistedFactory
    interface Factory {
        fun create(navKey: WeatherNavKey): WeatherViewModel
    }
}