package com.example.weatherforecast.Data.DataBase.room

sealed class WeatherLocalDataSrcState {
    object Loading : WeatherLocalDataSrcState()
    class Success(val data: Any) : WeatherLocalDataSrcState()
    class Error(val message: String) : WeatherLocalDataSrcState()
    object Empty : WeatherLocalDataSrcState()
}
