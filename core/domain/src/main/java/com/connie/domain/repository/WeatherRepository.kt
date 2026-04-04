package com.connie.domain.repository

import com.connie.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeatherFlow(query: String): Flow<Weather>
    fun getForecastFlow(query: String): Flow<List<Weather>>
}