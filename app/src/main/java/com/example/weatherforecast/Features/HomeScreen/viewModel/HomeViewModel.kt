package com.example.weatherforecast.Features.HomeScreen.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Data.Model.DailyForecastElement
import com.example.weatherforecast.Data.Network.ApiState
import com.example.weatherforecast.Data.Repository.RepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

/**
 * @class HomeViewModel
 * @brief ViewModel for managing weather forecast data.
 *
 * The `HomeViewModel` class is responsible for fetching weather data (current, hourly, and daily)
 * from the repository and exposing it to the UI through state flows.
 *
 * @param repository The repository used to fetch weather data.
 */
class HomeViewModel(private val repository: RepositoryImpl) : ViewModel() {

    /// Mutable state flow for the current weather data
    private val _weatherDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    /// Exposes the current weather data as an immutable state flow
    val weatherDataStateFlow: StateFlow<ApiState> get() = _weatherDataStateFlow

    /// Mutable state flow for the hourly forecast data
    private val _hourlyForecastDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    /// Exposes the hourly forecast data as an immutable state flow
    val hourlyForecastDataStateFlow: StateFlow<ApiState> get() = _hourlyForecastDataStateFlow

    /// Mutable state flow for the daily forecast data
    private val _dailyForecastDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    /// Exposes the daily forecast data as an immutable state flow
    val dailyForecastDataStateFlow: StateFlow<ApiState> get() = _dailyForecastDataStateFlow

    /**
     * Fetches the current weather data based on geographic coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     */
    fun fetchCurrentWeatherDataByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchCurrentWeather(lat, lon).catch {
                _weatherDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { weather ->
                _weatherDataStateFlow.value = ApiState.Success(weather)
            }
        }
    }

    /**
     * Fetches hourly weather forecast data based on geographic coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     */
    fun fetchHourlyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchHourlyForecast(lat, lon).catch {
                _hourlyForecastDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { hourly ->
                _hourlyForecastDataStateFlow.value = ApiState.Success(hourly)
            }
        }
    }

    /**
     * Fetches daily weather forecast data based on geographic coordinates.
     * Requires API level O for date processing.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchDailyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchDailyForecast(lat, lon).catch {
                _dailyForecastDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { dailyForecast ->
                val processedForecast = processForecastData(dailyForecast.list)
                _dailyForecastDataStateFlow.value = ApiState.Success(processedForecast)
            }
        }
    }
}

/**
 * Processes the daily forecast data by grouping it by day, and calculating the max and min temperatures for each day.
 *
 * @param forecastList List of daily forecast elements.
 * @return List of processed daily forecast elements.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun processForecastData(forecastList: List<DailyForecastElement>): List<DailyForecastElement> {
    return forecastList

        // grouping all forecast data from the same day together.
        .groupBy { element ->
            // Group by day (use LocalDate to avoid considering time)
            Instant.ofEpochSecond(element.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        //For each day's group, determine the highest (temp_max) and lowest (temp_min) temperatures.
        .map { (date, dailyElements) ->
            // Find max and min temps for the day
            val maxTemp: Double = dailyElements.maxOf { it.main.temp_max }
            val minTemp: Double = dailyElements.minOf { it.main.temp_min }

            //Create a new DailyForecastElement representing the day's forecast with the calculated max and min temperatures.
            dailyElements.first().copy(
                main = dailyElements.first().main.copy(temp_max = maxTemp, temp_min = minTemp)
            )
        }
}
