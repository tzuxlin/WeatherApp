package com.connie.weather.ui.weather

import com.connie.domain.model.City
import com.connie.domain.model.ViewState
import com.connie.domain.repository.CityRepository
import com.connie.domain.repository.WeatherRepository
import com.connie.domain.usecase.GetDailyForecastUseCase
import com.connie.domain.usecase.GetDefaultCityUseCase
import com.connie.domain.usecase.ToggleSavedCityUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getDailyForecastUseCase: GetDailyForecastUseCase
    private lateinit var getDefaultCityUseCase: GetDefaultCityUseCase
    private lateinit var toggleSavedCityUseCase: ToggleSavedCityUseCase
    private lateinit var cityRepository: CityRepository
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    private val testCity = City(name = "Taipei", country = "TW", lat = 25.0, lon = 121.5)
    private val weatherNavKey = WeatherNavKey(city = testCity)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getDailyForecastUseCase = mockk()
        getDefaultCityUseCase = mockk()
        toggleSavedCityUseCase = mockk()
        cityRepository = mockk()
        weatherRepository = mockk()

        // Default mocks for init
        every { cityRepository.getIsCitySavedFlow(any()) } returns flowOf(false)
        every { weatherRepository.getCurrentWeatherFlow(any()) } returns flowOf(null)
        every { weatherRepository.getForecastFlow(any(), any()) } returns flowOf(emptyList())
        every { getDailyForecastUseCase.invoke(any()) } returns flowOf(emptyList())
        coEvery { getDefaultCityUseCase.invoke() } returns testCity
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(navKey: WeatherNavKey = weatherNavKey): WeatherViewModel {
        return WeatherViewModel(
            weatherNavKey = navKey,
            getDailyForecastUseCase = getDailyForecastUseCase,
            getDefaultCityUseCase = getDefaultCityUseCase,
            toggleSavedCityUseCase = toggleSavedCityUseCase,
            cityRepository = cityRepository,
            weatherRepository = weatherRepository
        )
    }

    @Test
    fun `initialization should fetch data for city in navKey`() = runTest {
        // Given & When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        verify { weatherRepository.getCurrentWeatherFlow(testCity) }
        verify { weatherRepository.getForecastFlow(lon = testCity.lon, lat = testCity.lat) }
    }

    @Test
    fun `on ToggleSaved event, should invoke toggleSavedCityUseCase`() = runTest {
        // Given
        viewModel = createViewModel()
        coEvery { toggleSavedCityUseCase.invoke(testCity) } returns true

        // When
        viewModel.onUiEvent(WeatherUiEvent.ToggleSaved)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { toggleSavedCityUseCase.invoke(testCity) }
    }

    @Test
    fun `when toggleSavedCityUseCase returns false, should emit SavedLocationsLimitReached effect`() = runTest {
        // Given
        viewModel = createViewModel()
        coEvery { toggleSavedCityUseCase.invoke(testCity) } returns false

        val effects = mutableListOf<WeatherUiEffect>()
        val job = launch {
            viewModel.effect.collect { effects.add(it) }
        }

        // When
        viewModel.onUiEvent(WeatherUiEvent.ToggleSaved)
        advanceUntilIdle()

        // Then
        assertTrue(effects.contains(WeatherUiEffect.SavedLocationsLimitReached))
        job.cancel()
    }

    @Test
    fun `when fetching data fails, uiState should be updated to Error`() = runTest {
        // When (default setup: current weather flow emits null)
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.currentWeather is ViewState.Error)
        assertTrue(state.hourlyForecast is ViewState.Error)
        assertTrue(state.dailyForecast is ViewState.Error)
    }
}
