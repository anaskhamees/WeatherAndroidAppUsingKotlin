package com.example.weatherforecast.Features.Favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Data.DataBase.room.WeatherDataState
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.Data.Repository.Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouritesViewModel(private val repository: Repository) : ViewModel() {

    private val _weatherDataState = MutableStateFlow<WeatherDataState>(WeatherDataState.Loading)
    val weatherDataState: StateFlow<WeatherDataState> = _weatherDataState

    init {
        fetchAllWeatherData()
    }

    private fun fetchAllWeatherData() {
        viewModelScope.launch {
            repository.getAllWeatherData()
                .onStart { _weatherDataState.value = WeatherDataState.Loading }
                .catch { exception ->
                    _weatherDataState.value = WeatherDataState.Error(exception.message ?: "Error occurred")
                }
                .collect { weatherList ->
                    _weatherDataState.value = if (weatherList.isEmpty()) {
                        WeatherDataState.Empty
                    } else {
                        WeatherDataState.Success(weatherList)
                    }
                }
        }
    }

    fun insertWeatherData(weatherEntity: WeatherEntity) {
        viewModelScope.launch {
            repository.insertWeather(weatherEntity)
            fetchAllWeatherData() // Refresh data
        }
    }

    fun deleteWeatherData(weatherEntity: WeatherEntity) {
        viewModelScope.launch {
            repository.deleteWeather(weatherEntity)
            fetchAllWeatherData() // Refresh data
        }
    }

    suspend fun getWeatherCity(cityName: String): WeatherEntity? {
        return repository.getWeatherCity(cityName)
    }
}
