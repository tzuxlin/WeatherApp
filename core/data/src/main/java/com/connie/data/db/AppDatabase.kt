package com.connie.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.connie.data.db.dto.CityEntity
import com.connie.data.db.SavedCityDao

@Database(entities = [CityEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedCityDao(): SavedCityDao
}