package com.example.weatherforecast.Features.Favourites.viewModel

import com.example.weatherforecast.Data.Model.DailyForecast
import com.example.weatherforecast.Data.Model.Hourly
import com.example.weatherforecast.Data.Model.Weather
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.Data.Repository.Repository
import com.example.weatherforecast.Utils.Constants.CELSIUS_SHARED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A fake implementation of the [Repository] interface for testing purposes.
 * This class simulates a data repository by maintaining an in-memory list of weather entities.
 * It is designed to allow unit testing of view models without depending on a real database.
 */
class FakeRepository : Repository {
    // In-memory storage for weather data
    private val weatherData = mutableListOf<WeatherEntity>()

    // Default temperature unit
    private var temperatureUnit: String = CELSIUS_SHARED

    // Default wind speed unit
    private var windSpeedUnit: String = "km/h"

    // User notification preference
    private var notificationPreference: Boolean = true

    /**
     * Fetches the current weather for the specified latitude and longitude.
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @param lat The latitude of the location.
     * @param long The longitude of the location.
     * @return A flow of [Weather] data.
     */
    override fun fetchCurrentWeather(lat: Double, long: Double): Flow<Weather> {
        TODO("Not yet implemented")
    }

    /**
     * Fetches the hourly weather forecast for the specified latitude and longitude.
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A flow of [Hourly] data.
     */
    override fun fetchHourlyForecast(lat: Double, lon: Double): Flow<Hourly> {
        TODO("Not yet implemented")
    }

    /**
     * Fetches the daily weather forecast for the specified latitude and longitude.
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A flow of [DailyForecast] data.
     */
    override fun fetchDailyForecast(lat: Double, lon: Double): Flow<DailyForecast> {
        TODO("Not yet implemented")
    }

    /**
     * Retrieves the current temperature unit.
     *
     * @return A string representing the temperature unit (e.g., "Celsius").
     */
    override fun getTemperatureUnit(): String {
        return temperatureUnit
    }

    /**
     * Sets the temperature unit to the specified value.
     *
     * @param unit The new temperature unit (e.g., "Fahrenheit").
     */
    override fun setTemperatureUnit(unit: String) {
        temperatureUnit = unit
    }

    /**
     * Retrieves the current wind speed unit.
     *
     * @return A string representing the wind speed unit (e.g., "km/h").
     */
    override fun getWindSpeedUnit(): String {
        return windSpeedUnit
    }

    /**
     * Sets the wind speed unit to the specified value.
     *
     * @param unit The new wind speed unit (e.g., "m/s").
     */
    override fun setWindSpeedUnit(unit: String) {
        windSpeedUnit = unit
    }

    /**
     * Retrieves the user's notification preference.
     *
     * @return A boolean indicating whether notifications are enabled.
     */
    override fun getNotificationPreference(): Boolean {
        return notificationPreference
    }

    /**
     * Sets the user's notification preference to the specified value.
     *
     * @param enabled A boolean indicating whether notifications should be enabled or disabled.
     */
    override fun setNotificationPreference(enabled: Boolean) {
        notificationPreference = enabled
    }

    /**
     * Saves the specified location (latitude and longitude).
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     */
    override fun saveLocation(latitude: Float, longitude: Float) {
        TODO("Not yet implemented")
    }

    /**
     * Retrieves the saved location as a pair of latitude and longitude.
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @return A pair of floats representing the latitude and longitude, or null if not set.
     */
    override fun getLocation(): Pair<Float, Float>? {
        TODO("Not yet implemented")
    }

    /**
     * Retrieves the current language preference.
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @return A string representing the language preference.
     */
    override fun getLanguage(): String {
        TODO("Not yet implemented")
    }

    /**
     * Sets the language preference to the specified value.
     * This method is not yet implemented and will throw a NotImplementedError if called.
     *
     * @param language The new language preference.
     */
    override fun setLanguage(language: String) {
        TODO("Not yet implemented")
    }

    /**
     * Inserts the specified weather data into the repository.
     *
     * @param weather The [WeatherEntity] object to be inserted.
     */
    override suspend fun insertWeather(weather: WeatherEntity) {
        weatherData.add(weather)
    }

    /**
     * Retrieves all weather data as a flow of a list of [WeatherEntity].
     *
     * @return A flow that emits a list of all weather entities.
     */
    override fun getAllWeatherData(): Flow<List<WeatherEntity>> = flow {
        emit(weatherData)
    }

    /**
     * Deletes the specified weather data from the repository.
     *
     * @param weather The [WeatherEntity] object to be deleted.
     */
    override suspend fun deleteWeather(weather: WeatherEntity) {
        weatherData.remove(weather)
    }

    /**
     * Retrieves weather data for the specified city name.
     *
     * @param cityName The name of the city for which to retrieve weather data.
     * @return The corresponding [WeatherEntity], or null if not found.
     */
    override suspend fun getWeatherCity(cityName: String): WeatherEntity? {
        return weatherData.find { it.cityName == cityName }
    }
}
