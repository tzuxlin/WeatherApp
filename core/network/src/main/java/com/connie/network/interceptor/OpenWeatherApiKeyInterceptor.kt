package com.connie.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class OpenWeatherApiKeyInterceptor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter("APPID", apiKey)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
