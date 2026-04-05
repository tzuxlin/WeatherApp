package com.connie.domain.usecase

import com.connie.domain.model.Weather
import com.connie.domain.repository.CityRepository
import com.connie.domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import kotlin.collections.map

class GetWeatherForSavedLocationsUseCase @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun invoke(): Flow<List<Weather>> =
        cityRepository.getSavedCitiesFlow()
            .flatMapLatest { cities ->
                if (cities.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(cities.map(weatherRepository::getCurrentWeatherFlow)) { weathers ->
                        weathers.filterNotNull()
                    }
                }
            }
}