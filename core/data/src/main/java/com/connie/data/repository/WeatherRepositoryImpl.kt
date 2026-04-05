package com.connie.data.repository

import com.connie.domain.model.City
import com.connie.domain.model.Weather
import com.connie.domain.repository.WeatherRepository
import com.connie.network.api.WeatherApiService
import com.connie.network.model.CurrentWeatherResponse
import com.connie.network.model.ForecastResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherApiService
) : WeatherRepository {
    override fun getCurrentWeatherFlow(city: City) = flow<Weather?> {
        val responseBody = requestCurrentWeather(
            city = city.name,
            lat = city.lat,
            lon = city.lon,
        )
            ?: requestCurrentWeather(lat = city.lat, lon = city.lon)
            ?: requestCurrentWeather(city = city.name)
            ?: error("Failed to fetch the data")
        emit(buildWeather(responseBody))
    }.catch { emit(null) }


    override fun getForecastFlow(lon: String, lat: String) = flow {
        val response = service.getForecast(lat = lat, lon = lon)
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            emit(buildForecast(responseBody))
        } else {
            error("Failed to get the forecast data")
        }
    }.catch { emit(emptyList()) }


    private suspend fun requestCurrentWeather(
        city: String? = null,
        lat: String? = null,
        lon: String? = null,
    ): CurrentWeatherResponse? {
        val response = service.getCurrentWeather(
            city = city,
            lat = lat,
            lon = lon,
        )
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            null
        }
    }

    private fun buildWeather(response: CurrentWeatherResponse): Weather {
        return Weather(
            city = City(
                name = response.name,
                lat = BigDecimal(response.coord.lat).toString(),
                lon = BigDecimal(response.coord.lon).toString(),
                country = response.sys.country,
            ),
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
                city = City(
                    name = response.city.name,
                    lat = response.city.coord.toString(),
                    lon = response.city.coord.lon.toString(),
                    country = response.city.country,
                ),
                temperature = it.main.temp,
                feelsLike = it.main.feelsLike,
                minTemperature = it.main.tempMin,
                maxTemperature = it.main.tempMax,
                humidity = it.main.humidity,
                windSpeed = it.wind?.speed,
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