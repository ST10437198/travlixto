package com.travlixto.app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for any REST API that is NOT Firebase
 * (Firebase Auth/Firestore are accessed directly via their SDKs in the repository classes).
 *
 * Example use case: pulling live weather for a destination, or a third-party
 * places/travel API. Point BASE_URL at whatever backend you use, e.g. a
 * Firebase Cloud Function URL, or any public REST API.
 */
interface ApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("city") city: String,
        @Query("apikey") apiKey: String
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    val city: String,
    val tempCelsius: Double,
    val condition: String
)
