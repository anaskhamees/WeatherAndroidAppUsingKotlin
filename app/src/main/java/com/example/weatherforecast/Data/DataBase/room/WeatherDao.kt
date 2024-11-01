package com.example.weatherforecast.Data.DataBase.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.Data.Model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM Weather")
    fun getAllWeatherData(): Flow<List<WeatherEntity>>

    @Delete
    suspend fun deleteWeather(weather: WeatherEntity)

    @Query("SELECT * FROM Weather WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherByCity(cityName: String): WeatherEntity?
}

