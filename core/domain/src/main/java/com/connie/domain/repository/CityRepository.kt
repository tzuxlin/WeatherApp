package com.connie.domain.repository

import com.connie.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getSavedCitiesFlow(): Flow<List<City>>
    fun getIsCitySavedFlow(city: City): Flow<Boolean>
    suspend fun saveCity(city: City)
    suspend fun removeSavedCity(city: City)

    suspend fun getSavedCityCount(): Int
}