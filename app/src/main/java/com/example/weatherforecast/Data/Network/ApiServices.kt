package com.example.weatherforecast.Data.Network

import com.example.weatherforecast.Data.Model.DailyForecast
import com.example.weatherforecast.Data.Model.Hourly
import com.example.weatherforecast.Data.Model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): retrofit2.Response<Weather>

    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): retrofit2.Response<Hourly>

    @GET("forecast")
    suspend fun getDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): retrofit2.Response<DailyForecast>
}