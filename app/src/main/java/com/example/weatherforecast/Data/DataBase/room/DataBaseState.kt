package com.example.weatherforecast.Data.DataBase.room

sealed class WeatherDataState {
    object Loading : WeatherDataState()
    class Success(val data: Any) : WeatherDataState()
    class Error(val message: String) : WeatherDataState()
    object Empty : WeatherDataState()
}
