package com.example.weatherforecast.Data.DataBase.local.favourites

import com.example.weatherforecast.Data.Model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertWeather(weather: WeatherEntity)
    fun getAllWeatherData(): Flow<List<WeatherEntity>>
    suspend fun deleteWeather(weather: WeatherEntity)
    suspend fun getWeatherCity(cityName: String): WeatherEntity?
}