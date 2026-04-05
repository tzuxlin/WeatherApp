package com.connie.data.repository

import com.connie.data.db.dto.CityEntity
import com.connie.data.db.dto.SavedCityDao
import com.connie.domain.model.City
import com.connie.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val savedCityDao: SavedCityDao,
) : CityRepository {

    override fun getSavedCitiesFlow(): Flow<List<City>> =
        savedCityDao.getSavedCities()
            .map { cities -> cities.map(::toDomainCity) }

    override fun getIsCitySavedFlow(city: City): Flow<Boolean> =
        savedCityDao.isCitySaved(
            name = city.name,
            country = city.country,
        )

    override suspend fun saveCity(city: City) {
        savedCityDao.insertCity(toCityEntity(city))
    }

    override suspend fun removeSavedCity(city: City) {
        savedCityDao.deleteCity(
            name = city.name,
            country = city.country,
        )
    }

    override suspend fun getSavedCityCount(): Int {
        return savedCityDao.getSavedCityCount()
    }

    private fun toDomainCity(entity: CityEntity): City = City(
        name = entity.name,
        country = entity.country,
        lat = entity.lat,
        lon = entity.lon,
    )

    private fun toCityEntity(city: City): CityEntity = CityEntity(
        name = city.name,
        country = city.country,
        lat = city.lat,
        lon = city.lon,
    )

}