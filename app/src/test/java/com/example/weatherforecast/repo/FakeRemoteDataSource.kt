package com.example.weatherforecast.repo

import com.example.weatherforecast.Data.Network.RemoteDataSource
import com.example.weatherforecast.Data.Model.Clouds
import com.example.weatherforecast.Data.Model.Coord
import com.example.weatherforecast.Data.Model.DailyForecast
import com.example.weatherforecast.Data.Model.Hourly
import com.example.weatherforecast.Data.Model.Main
import com.example.weatherforecast.Data.Model.Sys
import com.example.weatherforecast.Data.Model.Weather
import com.example.weatherforecast.Data.Model.WeatherElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource : RemoteDataSource {

    private val weatherTestData = Weather(
        visibility = 10000,
        timezone = 3600,
        main = Main(
            feels_like = 283.0,
            humidity = 80,
            pressure = 1012,
            temp = 283.0,
            temp_max = 283.0,
            temp_min = 283.0
        ),
        clouds = Clouds(all = 10),
        sys = Sys(
            country = "US",
            sunrise = 1600000000,
            sunset = 1600040000,
            id = 1,
            type = 1
        ),
        dt = 1600000000,
        coord = Coord(lon = -122.08, lat = 37.39),
        weather = listOf(
            WeatherElement(
                icon = "01d",
                description = "Clear sky",
                main = "Clear",
                id = 800
            )
        ),
        name = "San Francisco",
        cod = 200,
        id = 12345,
        base = "stations",
        wind = com.example.weatherforecast.Data.Model.Wind(
            deg = 180,
            speed = 5.0,
            gust = 8.0
        )
    )

    override fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather> {
        return flow {
            emit(weatherTestData)
        }
    }

    override fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly> {
        TODO("Not yet implemented")
    }

    override fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast> {
        TODO("Not yet implemented")
    }
}