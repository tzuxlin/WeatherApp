package com.connie.domain.repository

import com.connie.domain.model.City
import kotlinx.coroutines.flow.Flow

interface GeoRepository {
    fun getDirectGeocoding(cityName: String): Flow<List<City>>
}