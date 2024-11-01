package com.example.weatherforecast.Data.Network

import android.util.Log
import com.example.weatherforecast.Data.DataBase.sharedPrefrences.SharedPrefsDataSource
import com.example.weatherforecast.Data.Model.DailyForecast
import com.example.weatherforecast.Data.Model.Hourly
import com.example.weatherforecast.Data.Model.Weather
import com.example.weatherforecast.Utils.Constants
import com.example.weatherforecast.Utils.Constants.ENGLISH_SHARED
import com.example.weatherforecast.Utils.Constants.LANGUAGE_SHARED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl(
    private val apiService: ApiServices,
    private val sharedPrefsDataSource: SharedPrefsDataSource
) : RemoteDataSource {

    override fun fetchCurrentWeather(lat: Double, lon: Double): Flow<Weather> = flow {
        val lang = sharedPrefsDataSource.getString(LANGUAGE_SHARED, ENGLISH_SHARED)
        val response = apiService.getWeather(lat, lon, Constants.API_KEY, Constants.UNITS, lang)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving weather data")
        }
    }

    override fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly> = flow {
        val lang = sharedPrefsDataSource.getString(LANGUAGE_SHARED, ENGLISH_SHARED)
        val response =
            apiService.getHourlyForecast(lat, lon, Constants.API_KEY, Constants.UNITS, lang)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving hourly forecast data")
        }
    }

    override fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast> = flow {
        val lang = sharedPrefsDataSource.getString(LANGUAGE_SHARED, ENGLISH_SHARED)
        val response =
            apiService.getDailyForecast(lat, lon, Constants.API_KEY, Constants.UNITS, lang)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving daily forecast data")
        }
    }
}