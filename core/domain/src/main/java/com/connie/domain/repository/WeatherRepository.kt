package com.connie.domain.repository

import com.connie.domain.model.City
import com.connie.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeatherFlow(city: City): Flow<Weather?>
    fun getForecastFlow(lon: Double, lat: Double): Flow<List<Weather>>
}