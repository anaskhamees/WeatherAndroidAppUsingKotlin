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

/**
 * A fake implementation of the RemoteDataSource interface for testing purposes.
 * This class simulates remote data fetching by returning pre-defined weather data.
 */
class FakeRemoteDataSource : RemoteDataSource {

    // Sample weather data to be returned by the fake data source
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

    /**
     * Fetches the current weather based on the provided latitude and longitude.
     *
     * @param lat The latitude of the location for which to fetch the weather.
     * @param long The longitude of the location for which to fetch the weather.
     * @return A Flow that emits the current Weather data.
     */
    override fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather> {
        return flow {
            emit(weatherTestData) // Emit the predefined weather data
        }
    }

    /**
     * Fetches the hourly weather forecast for a given latitude and longitude.
     * This method is not yet implemented.
     *
     * @param lat The latitude of the location for which to fetch the hourly forecast.
     * @param lon The longitude of the location for which to fetch the hourly forecast.
     * @return A Flow that emits Hourly data (not yet implemented).
     */
    override fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly> {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Fetches the daily weather forecast for a given latitude and longitude.
     * This method is not yet implemented.
     *
     * @param lat The latitude of the location for which to fetch the daily forecast.
     * @param lon The longitude of the location for which to fetch the daily forecast.
     * @return A Flow that emits DailyForecast data (not yet implemented).
     */
    override fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast> {
        TODO("Not yet implemented") // Placeholder for future implementation
    }
}
