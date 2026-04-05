package com.connie.domain

import com.connie.domain.model.City
import com.connie.domain.repository.CityRepository
import com.connie.domain.usecase.ToggleSavedCityUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleSavedCityUseCaseTest {

    private lateinit var repository: CityRepository
    private lateinit var useCase: ToggleSavedCityUseCase

    private val testCity = City(name = "Taipei", country = "TW", lat = 25.0, lon = 121.5)

    private companion object {
        /** Must match [ToggleSavedCityUseCase] limit. */
        private const val SAVED_CITIES_LIMIT = 5
    }

    @Before
    fun setup() {
        repository = mockk()
        useCase = ToggleSavedCityUseCase(repository)
    }

    @Test
    fun `when city is already saved, invoke should remove it and return true`() = runTest {
        // Given
        every { repository.getIsCitySavedFlow(testCity) } returns flowOf(true)
        coEvery { repository.removeSavedCity(testCity) } returns Unit

        // When
        val result = useCase.invoke(testCity)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { repository.removeSavedCity(testCity) }
        coVerify(exactly = 0) { repository.saveCity(any()) }
        coVerify(exactly = 0) { repository.getSavedCityCount() }
    }

    @Test
    fun `when city is not saved and limit not reached, invoke should save it and return true`() = runTest {
        // Given
        every { repository.getIsCitySavedFlow(testCity) } returns flowOf(false)
        coEvery { repository.getSavedCityCount() } returns SAVED_CITIES_LIMIT - 1
        coEvery { repository.saveCity(testCity) } returns Unit

        // When
        val result = useCase.invoke(testCity)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { repository.saveCity(testCity) }
        coVerify(exactly = 0) { repository.removeSavedCity(any()) }
    }

    @Test
    fun `when city is not saved and limit is reached, invoke should return false and not save`() = runTest {
        // Given
        every { repository.getIsCitySavedFlow(testCity) } returns flowOf(false)
        coEvery { repository.getSavedCityCount() } returns SAVED_CITIES_LIMIT

        // When
        val result = useCase.invoke(testCity)

        // Then
        assertFalse(result)
        coVerify(exactly = 0) { repository.saveCity(any()) }
        coVerify(exactly = 0) { repository.removeSavedCity(any()) }
    }
}
