package com.example.weatherforecast.repo

import com.example.weatherforecast.Data.DataBase.local.favourites.LocalDataSource
import com.example.weatherforecast.Data.Model.WeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A fake implementation of the LocalDataSource interface for testing purposes.
 * This class simulates a local data source by storing weather data in-memory.
 */
class FakeLocalDataSource : LocalDataSource {
    // A mutable list to store WeatherEntity objects in memory
    private val weatherList = mutableListOf<WeatherEntity>()

    /**
     * Inserts a WeatherEntity into the in-memory list.
     *
     * @param weather The WeatherEntity to be inserted.
     */
    override suspend fun insertWeather(weather: WeatherEntity) {
        weatherList.add(weather) // Add the weather entity to the list
    }

    /**
     * Retrieves all weather data stored in the local data source.
     *
     * @return A Flow that emits a list of WeatherEntity objects.
     */
    override fun getAllWeatherData(): Flow<List<WeatherEntity>> {
        return flow {
            emit(weatherList) // Emit the current list of weather entities
        }
    }

    /**
     * Deletes a WeatherEntity from the in-memory list.
     *
     * @param weather The WeatherEntity to be deleted.
     */
    override suspend fun deleteWeather(weather: WeatherEntity) {
        weatherList.remove(weather) // Remove the specified weather entity from the list
    }

    /**
     * Retrieves a WeatherEntity for a specified city name.
     *
     * @param cityName The name of the city to search for.
     * @return The WeatherEntity for the city if found, or null if not found.
     */
    override suspend fun getWeatherCity(cityName: String): WeatherEntity? {
        return weatherList.find { it.cityName == cityName } // Find and return the weather entity for the city
    }
}
