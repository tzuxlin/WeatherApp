package com.connie.network.api

import com.connie.network.model.CurrentWeatherResponse
import com.connie.network.model.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
        @Query("units") units: String = "metric",
        @Query("APPID") appId: String = "4baedbc0801002265fb319221180418b",
    ): Response<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") query: String,
        @Query("units") units: String = "metric",
        @Query("APPID") appId: String = "4baedbc0801002265fb319221180418b",
    ): Response<ForecastResponse>
}