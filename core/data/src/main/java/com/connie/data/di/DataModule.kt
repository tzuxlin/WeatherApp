package com.connie.data.di

import com.connie.data.repository.GeoRepositoryImpl
import com.connie.data.repository.WeatherRepositoryImpl
import com.connie.domain.repository.GeoRepository
import com.connie.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindGeoRepository(
        geoRepositoryImpl: GeoRepositoryImpl
    ): GeoRepository
}
