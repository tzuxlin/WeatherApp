package com.connie.domain.usecase

import com.connie.domain.model.City
import com.connie.domain.repository.GeoRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetDefaultCityUseCase @Inject constructor(
    private val geoRepository: GeoRepository,
) {
    suspend fun invoke(): City? {
        // Hardcoded default city (Taipei) for demo purpose only.
        return geoRepository.getDirectGeocoding("Taipei").firstOrNull()?.firstOrNull()
    }
}