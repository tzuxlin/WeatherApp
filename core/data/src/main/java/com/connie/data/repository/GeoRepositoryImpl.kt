package com.connie.data.repository

import com.connie.domain.model.City
import com.connie.domain.repository.GeoRepository
import com.connie.network.api.WeatherApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GeoRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
) : GeoRepository {
    override fun getDirectGeocoding(cityName: String): Flow<List<City>> = flow {
        val response = weatherApiService.getDirectGeocoding(cityName)
        if (response.isSuccessful) {
            response.body()?.let { locations ->
                val cities = locations.map {
                    City(
                        name = it.name,
                        lat = it.lat,
                        lon = it.lon,
                        country = it.country,
                    )
                }
                emit(cities)
            }
        }
    }
}