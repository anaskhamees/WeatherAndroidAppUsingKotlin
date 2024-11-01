package com.example.weatherforecast.Data.Repository

import com.example.weatherforecast.Data.Model.DailyForecast
import com.example.weatherforecast.Data.Model.Hourly
import com.example.weatherforecast.Data.Model.Weather
import com.example.weatherforecast.Data.Model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface Repository {
    //remote methods
    fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather>
    fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly>
    fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast>

    //SharedPrefs Methods
    fun getTemperatureUnit(): String
    fun setTemperatureUnit(unit: String)
    fun getWindSpeedUnit(): String
    fun setWindSpeedUnit(unit: String)
    fun saveLocation(latitude: Float, longitude: Float)
    fun getLocation(): Pair<Float, Float>?
    fun getNotificationPreference(): Boolean
    fun setNotificationPreference(enabled: Boolean)
    fun getLanguage(): String
    fun setLanguage(language: String)

    //local database
    suspend fun insertWeather(weather: WeatherEntity)
    fun getAllWeatherData(): Flow<List<WeatherEntity>>
    suspend fun deleteWeather(weather: WeatherEntity)
    suspend fun getWeatherCity(cityName: String): WeatherEntity?

}