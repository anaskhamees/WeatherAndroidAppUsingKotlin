package com.example.weathercast.DataSrc.remote

import android.util.Log
import com.example.iti.data.db.sharedPrefrences.SharedPrefsDataSource
import com.example.weatherforecast.Network.ApiServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl(
    private val apiService: ApiServices,
    private val sharedPrefsDataSource: SharedPrefsDataSource
) : RemoteDataSource {

    override fun fetchCurrentWeather(lat: Double, lon: Double): Flow<Weather> = flow {
        val lang = sharedPrefsDataSource.getString(LANGUAGE_SHARED, ENGLISH_SHARED)
        val response = apiService.getWeather(lat, lon, Constants.API_KEY, Constants.UNITS, lang)
        Log.e("A&A", "fetchCurrentWeather: $lang")
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