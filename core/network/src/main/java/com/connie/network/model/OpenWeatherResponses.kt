package com.connie.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoordResponse(
    val lon: Double,
    val lat: Double,
)

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

@JsonClass(generateAdapter = true)
data class MainResponse(
    val temp: Double,
    @Json(name = "feels_like")
    val feelsLike: Double,
    @Json(name = "temp_min")
    val tempMin: Double,
    @Json(name = "temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @Json(name = "sea_level")
    val seaLevel: Int,
    @Json(name = "grnd_level")
    val groundLevel: Int,
)

@JsonClass(generateAdapter = true)
data class WindResponse(
    val speed: Double,
    val deg: Int,
)

@JsonClass(generateAdapter = true)
data class CloudsResponse(
    val all: Int,
)

@JsonClass(generateAdapter = true)
data class SysResponse(
    val type: Int,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)


@JsonClass(generateAdapter = true)
data class CityResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "coord")
    val coord: CoordResponse,
    @Json(name = "country")
    val country: String,
    @Json(name = "population")
    val population: Int,
    @Json(name = "timezone")
    val timezone: Int,
    @Json(name = "sunrise")
    val sunrise: Long,
    @Json(name = "sunset")
    val sunset: Long
)