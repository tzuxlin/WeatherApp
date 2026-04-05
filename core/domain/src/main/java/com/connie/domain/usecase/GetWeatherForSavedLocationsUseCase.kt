package com.connie.domain.usecase

import com.connie.domain.model.City
import com.connie.domain.model.Weather
import com.connie.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.collections.map

class GetWeatherForSavedLocationsUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    fun invoke(): Flow<List<Weather>> {
        // TODO update logic
        val city = City("", "25.0478", "121.5319", "")
        val locations = listOf(city, city, city)
        return combine(locations.map { weatherRepository.getCurrentWeatherFlow(it) }) {
            it.filterNotNull().toList()
        }
    }
}