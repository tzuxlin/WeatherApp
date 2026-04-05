package com.connie.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    @Json(name = "list")
    val items: List<ForecastItem>,
    @Json(name = "city")
    val city: CityResponse
)

@JsonClass(generateAdapter = true)
data class ForecastItem(
    @Json(name = "dt")
    val timestamp: Long,
    @Json(name = "main")
    val main: MainResponse,
    @Json(name = "weather")
    val weather: List<WeatherResponse>,
    @Json(name = "clouds")
    val clouds: CloudsResponse?,
    @Json(name = "wind")
    val wind: WindResponse?,
    @Json(name = "visibility")
    val visibility: Int?,
    @Json(name = "pop")
    val probabilityOfPrecipitation: Double?,
    @Json(name = "dt_txt")
    val dateTimeText: String?,
)