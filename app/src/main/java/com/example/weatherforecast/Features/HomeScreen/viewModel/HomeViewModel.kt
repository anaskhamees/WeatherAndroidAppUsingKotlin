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

class HomeViewModel(private val repository: RepositoryImpl) : ViewModel() {

    private val _weatherDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDataStateFlow: StateFlow<ApiState> get() = _weatherDataStateFlow

    private val _hourlyForecastDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val hourlyForecastDataStateFlow: StateFlow<ApiState> get() = _hourlyForecastDataStateFlow

    private val _dailyForecastDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val dailyForecastDataStateFlow: StateFlow<ApiState> get() = _dailyForecastDataStateFlow


    fun fetchCurrentWeatherDataByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchCurrentWeather(lat, lon).catch {
                _weatherDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { weather ->
                _weatherDataStateFlow.value = ApiState.Success(weather)
            }
        }
    }

    fun fetchHourlyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchHourlyForecast(lat, lon).catch {
                _hourlyForecastDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { hourly ->
                _hourlyForecastDataStateFlow.value = ApiState.Success(hourly)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchDailyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchDailyForecast(lat, lon).catch {
                _dailyForecastDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { dailyForecast ->
                Log.e("HomeViewModel", "Daily Forecast: $dailyForecast")
                val processedForecast = processForecastData(dailyForecast.list)
                _dailyForecastDataStateFlow.value = ApiState.Success(processedForecast)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun processForecastData(forecastList: List<DailyForecastElement>): List<DailyForecastElement> {
    return forecastList
        .groupBy { element ->
            // Group by day (use LocalDate to avoid considering time)
            Instant.ofEpochSecond(element.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        .map { (date, dailyElements) ->
            // Find max and min temps for the day
            val maxTemp = dailyElements.maxOf { it.main.temp_max }
            val minTemp = dailyElements.minOf { it.main.temp_min }

            // Use the first element for other fields, or customize as needed
            dailyElements.first().copy(
                main = dailyElements.first().main.copy(temp_max = maxTemp, temp_min = minTemp)
            )
        }
}
