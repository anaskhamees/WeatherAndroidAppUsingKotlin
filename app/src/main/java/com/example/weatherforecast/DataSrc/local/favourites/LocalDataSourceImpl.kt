package com.example.weatherforecast.DataSrc.favourites

import LocalDataSource
import com.example.weatherforecast.DataSrc.room.WeatherDao
import com.example.weatherforecast.Model.WeatherEntity


import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val weatherDao: WeatherDao) : LocalDataSource {
    override suspend fun insertWeather(weather: WeatherEntity) {
        return weatherDao.insertWeather(weather)
    }

    override fun getAllWeatherData(): Flow<List<WeatherEntity>> {
        return weatherDao.getAllWeatherData()
    }

    override suspend fun deleteWeather(weather: WeatherEntity) {
        return weatherDao.deleteWeather(weather)
    }

    override suspend fun getWeatherCity(cityName: String): WeatherEntity? {
        return weatherDao.getWeatherByCity(cityName)
    }

}