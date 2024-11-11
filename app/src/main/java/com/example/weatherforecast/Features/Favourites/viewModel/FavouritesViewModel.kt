package com.example.weatherforecast.Features.Favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Data.DataBase.room.WeatherLocalDataSrcState
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.Data.Repository.Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouritesViewModel(private val repository: Repository) : ViewModel() {

    private val _weatherLocalDataSrcState = MutableStateFlow<WeatherLocalDataSrcState>(WeatherLocalDataSrcState.Loading)
    val weatherLocalDataSrcState: StateFlow<WeatherLocalDataSrcState> = _weatherLocalDataSrcState

    init {
        fetchAllWeatherData()
    }

    private fun fetchAllWeatherData() {
        viewModelScope.launch {
            repository.getAllWeatherData()
                .onStart { _weatherLocalDataSrcState.value = WeatherLocalDataSrcState.Loading }
                .catch { exception ->
                    _weatherLocalDataSrcState.value = WeatherLocalDataSrcState.Error(exception.message ?: "Error occurred")
                }
                .collect { weatherList ->
                    _weatherLocalDataSrcState.value = if (weatherList.isEmpty()) {
                        WeatherLocalDataSrcState.Empty
                    } else {
                        WeatherLocalDataSrcState.Success(weatherList)
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
