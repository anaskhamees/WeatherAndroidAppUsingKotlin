package com.example.weathercast.DataSrc.remote

import com.example.weatherforecast.Model.DailyForecast
import com.example.weatherforecast.Model.Hourly
import com.example.weatherforecast.Model.Weather
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    abstract val LANGUAGE_SHARED: String

    fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather>
    fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly>
    fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast>
}