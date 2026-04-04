package com.connie.domain.usecase

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
        val locations = listOf("Taipei", "Taichung", "Tainan", "Tokyo", "Osaka", "Texas")
        return combine(locations.map { weatherRepository.getCurrentWeatherFlow(it) }) {
            it.toList()
        }
    }
}