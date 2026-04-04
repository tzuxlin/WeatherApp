package com.connie.domain.model

data class Weather(
    val cityName: String,
    val countryCode: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val main: String,
    val description: String,
    val iconCode: String,
    val timestamp: Long,
    val sunrise: Long,
    val sunset: Long,
    val timeZone: Int,
)