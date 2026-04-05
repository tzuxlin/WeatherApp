package com.connie.domain.usecase

import com.connie.domain.model.City
import com.connie.domain.repository.CityRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ToggleSavedCityUseCase @Inject constructor(
    private val cityRepository: CityRepository,
) {

    /**
     * Return false if saving is blocked by the saved-city limit
     */
    suspend fun invoke(city: City): Boolean {
        return cityRepository.getIsCitySavedFlow(city)
            .map { isSaved ->
                if (isSaved) {
                    cityRepository.removeSavedCity(city)
                    true
                } else {
                    if (cityRepository.getSavedCityCount() >= SAVED_CITIES_LIMIT) {
                        false
                    } else {
                        cityRepository.saveCity(city)
                        true
                    }
                }
            }.first()
    }

    companion object {
        private const val SAVED_CITIES_LIMIT = 5
    }
}