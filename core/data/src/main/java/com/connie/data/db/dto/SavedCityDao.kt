package com.connie.data.db.dto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity): Long

    @Query("SELECT * FROM saved_cities ORDER BY name ASC")
    fun getSavedCities(): Flow<List<CityEntity>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM saved_cities
            WHERE name = :name AND country = :country
        )
    """)
    fun isCitySaved(
        name: String,
        country: String,
    ): Flow<Boolean>

    @Query("""
        DELETE FROM saved_cities
        WHERE name = :name AND country = :country
    """)
    suspend fun deleteCity(
        name: String,
        country: String,
    ): Int

    @Query("SELECT COUNT(*) FROM saved_cities")
    suspend fun getSavedCityCount(): Int
}
