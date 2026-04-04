package com.connie.weather.mapper

object OpenWeatherResolver {
    fun resolveIcon(iconCode: String?): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }
}