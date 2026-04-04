package com.connie.data.repository

import com.connie.domain.model.Weather
import com.connie.domain.repository.WeatherRepository
import com.connie.network.api.WeatherApiService
import com.connie.network.model.CurrentWeatherResponse
import com.connie.network.model.ForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherApiService
) : WeatherRepository {
    override fun getCurrentWeatherFlow(query: String): Flow<Weather> {
        return flow {
            val response = service.getCurrentWeather(query)
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                emit(buildWeather(responseBody))
            } else {
                error("TODO")
            }
        }
    }

    override fun getForecastFlow(query: String): Flow<List<Weather>> {
        return flow {
            val response = service.getForecast(query)
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                emit(buildForecast(responseBody))
            } else {
                error("TODO")
            }
        }
    }

    private fun buildWeather(response: CurrentWeatherResponse): Weather {
        return Weather(
            cityName = response.name,
            countryCode = response.sys.country,
            temperature = response.main.temp,
            feelsLike = response.main.feelsLike,
            minTemperature = response.main.tempMin,
            maxTemperature = response.main.tempMax,
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            main = response.weather[0].main,
            description = response.weather[0].description,
            iconCode = response.weather[0].icon,
            timestamp = response.timestamp,
            sunrise = response.sys.sunrise,
            sunset = response.sys.sunset,
            timeZone = response.timezone,
        )
    }

    private fun buildForecast(response: ForecastResponse): List<Weather> {
        return response.items.map {
            Weather(
                cityName = response.city.name,
                countryCode = response.city.country,
                temperature = it.main.temp,
                feelsLike = it.main.feelsLike,
                minTemperature = it.main.tempMin,
                maxTemperature = it.main.tempMax,
                humidity = it.main.humidity,
                windSpeed = it.wind.speed,
                main = it.weather[0].main,
                description = it.weather[0].description,
                iconCode = it.weather[0].icon,
                timestamp = it.timestamp,
                sunrise = response.city.sunrise,
                sunset = response.city.sunset,
                timeZone = response.city.timezone,
            )
        }
    }

}