package com.connie.domain.usecase

import com.connie.domain.model.Weather
import com.connie.domain.repository.WeatherRepository
import com.connie.domain.util.TimeHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDailyForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    fun invoke(query: String): Flow<List<Weather>> {
        return weatherRepository.getForecastFlow(query)
            .map { forecastItems ->
                forecastItems
                    .groupBy { weather ->
                        TimeHelper.getStartOfDay(
                            timestampSeconds = weather.timestamp,
                            timezone = weather.timeZone,
                        ).toEpochSecond()
                    }
                    .map { (_, weathers) ->
                        weathers.first().copy(
                            minTemperature = weathers.minOf { it.temperature },
                            maxTemperature = weathers.maxOf { it.temperature },
                        )
                    }
            }
    }
}