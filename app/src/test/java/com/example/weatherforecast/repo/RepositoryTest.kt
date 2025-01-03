package com.example.weatherforecast.repo

import com.example.weatherforecast.Data.DataBase.local.favourites.LocalDataSource
import com.example.weatherforecast.Data.Network.RemoteDataSource
import com.example.weatherforecast.Data.DataBase.sharedPrefrences.SharedPrefsDataSource
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.Data.Repository.Repository
import com.example.weatherforecast.Data.Repository.RepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit test class for the Repository implementation.
 * This class tests the behavior of the Repository
 * using fake data sources for local, remote, and shared preferences.
 */
class RepositoryTest {
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var sharedPrefsDataSource: SharedPrefsDataSource
    private lateinit var repository: Repository

    /**
     * Sets up the test environment before each test case.
     * Initializes the local, remote, and shared preferences data sources
     * and creates an instance of the Repository implementation.
     */
    @Before
    fun setup() {
        localDataSource = FakeLocalDataSource()
        remoteDataSource = FakeRemoteDataSource()
        sharedPrefsDataSource = FakeSharedPrefsDataSource()
        repository = RepositoryImpl(remoteDataSource, sharedPrefsDataSource, localDataSource)
    }

    /**
     * Tests the insertion of weather data into the repository.
     * It verifies that the inserted data can be retrieved successfully.
     */
    @Test
    fun `test insert weather data`() = runTest {
        val fakeWeatherEntity = fakeWeatherEntity
        repository.insertWeather(fakeWeatherEntity)
        val weatherData = repository.getAllWeatherData().first()
        assertEquals(listOf(fakeWeatherEntity), weatherData)
    }

    /**
     * Tests the retrieval of weather data when no data is present.
     * It ensures that an empty list is returned.
     */
    @Test
    fun test_retrieve_nothing_weather_data() = runTest {
        val weatherData = repository.getAllWeatherData().first()
        assertEquals(emptyList<WeatherEntity>(), weatherData)
    }

    /**
     * Tests the deletion of weather data from the repository.
     * It verifies that after deletion, no weather data remains in the repository.
     */
    @Test
    fun test_delete_weather_data() = runTest {
        val fakeWeatherEntity = fakeWeatherEntity
        repository.insertWeather(fakeWeatherEntity)
        repository.deleteWeather(fakeWeatherEntity)
        val weatherData = repository.getAllWeatherData().first()
        assertEquals(emptyList<WeatherEntity>(), weatherData)
    }

    /**
     * Tests the insertion of multiple weather data entries.
     * It verifies that the repository can handle duplicate entries
     * and correctly manages deletions.
     */
    @Test
    fun test_inserting_more_than_one_weather_data() = runTest {
        val fakeWeatherEntity = fakeWeatherEntity
        val fakeWeatherEntity2 = fakeWeatherEntity2
        repository.insertWeather(fakeWeatherEntity)
        repository.insertWeather(fakeWeatherEntity2)
        repository.insertWeather(fakeWeatherEntity2)
        repository.deleteWeather(fakeWeatherEntity)
        val weatherData = repository.getAllWeatherData().first()
        assertEquals(listOf(fakeWeatherEntity2, fakeWeatherEntity2), weatherData)
    }

    /**
     * Tests the scenario when trying to retrieve weather data
     * for a city that does not exist in the repository.
     * It ensures that the method returns null for a non-existent city.
     */
    @Test
    fun test_get_weather_city_not_found() = runTest {
        val weatherCity = repository.getWeatherCity("Cairo")
        assertEquals(null, weatherCity)
    }

    /**
     * Tests the retrieval of weather data for a specific city.
     * It verifies that the correct weather entity is returned.
     */
    @Test
    fun test_get_weather_city() = runTest {
        val fakeWeatherEntity = fakeWeatherEntity
        repository.insertWeather(fakeWeatherEntity)
        val weatherCity = repository.getWeatherCity("Cairo")
        assertEquals(fakeWeatherEntity, weatherCity)
    }

    /**
     * Tests fetching the current weather data from the remote data source.
     * It asserts that the fetched data matches expected values.
     */
    @Test
    fun test_fetch_current_weather() = runTest {
        val weather = repository.fetchCurrentWeather(37.39, -122.08).first()
        assertEquals("San Francisco", weather.name)
        assertEquals("Clear", weather.weather[0].main)
        assertEquals(283, weather.main.temp.toInt())
    }

    // Fake weather entity used for testing
    val fakeWeatherEntity = WeatherEntity(
        cityName = "Cairo",
        description = "Clear Sky",
        currentTemp = 30.0,
        minTemp = 25.0,
        maxTemp = 35.0,
        pressure = 1012,
        humidity = 50,
        windSpeed = 5.0,
        clouds = 10,
        sunrise = 1600000000,
        sunset = 1600040000,
        date = "2024-09-28",
        latitude = 30.0444,
        longitude = 31.2357,
        lottie = 1
    )

    // Another fake weather entity used for testing
    val fakeWeatherEntity2 = WeatherEntity(
        cityName = "Alexandria",
        description = "Clear Sky",
        currentTemp = 30.0,
        minTemp = 25.0,
        maxTemp = 35.0,
        pressure = 1012,
        humidity = 50,
        windSpeed = 5.0,
        clouds = 10,
        sunrise = 1600000000,
        sunset = 1600040000,
        date = "2024-09-28",
        latitude = 30.0444,
        longitude = 31.2357,
        lottie = 1
    )
}
