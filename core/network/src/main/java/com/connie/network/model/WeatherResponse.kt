package com.connie.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentWeatherResponse(
    val coord: CoordResponse,
    val weather: List<WeatherResponse>,
    val base: String,
    val main: MainResponse,
    val visibility: Int,
    val wind: WindResponse,
    val clouds: CloudsResponse,
    @Json(name = "dt")
    val timestamp: Long,
    val sys: SysResponse,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int,
)

