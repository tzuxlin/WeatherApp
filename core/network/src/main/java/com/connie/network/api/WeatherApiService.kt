package com.connie.network.api

import com.connie.network.model.CurrentWeatherResponse
import com.connie.network.model.ForecastResponse
import com.connie.network.model.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String?,
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("units") units: String = "metric",
        @Query("APPID") appId: String = "4baedbc0801002265fb319221180418b",
    ): Response<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String = "metric",
        @Query("APPID") appId: String = "4baedbc0801002265fb319221180418b",
    ): Response<ForecastResponse>

    @GET("geo/1.0/direct")
    suspend fun getDirectGeocoding(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("APPID") apiKey: String = "4baedbc0801002265fb319221180418b",
    ): Response<List<GeocodingResponse>>
}