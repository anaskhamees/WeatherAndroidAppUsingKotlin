package com.example.weatherforecast.Features.Favourites.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.Data.Repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// This class contains unit tests for the FavouritesViewModel, which manages the
// weather data in the context of a favorites feature. The tests are executed
// using the AndroidJUnit4 runner to facilitate Android-specific functionality.
@RunWith(AndroidJUnit4::class)
class FavouritesViewModelTest {
    // The FavouritesViewModel instance to be tested
    private lateinit var viewModel: FavouritesViewModel

    // A mock implementation of the Repository interface
    private lateinit var repository: Repository

    // Setup method to initialize the ViewModel and repository before each test
    @Before
    fun setup() {
        repository = FakeRepository() // Use a fake repository for testing
        viewModel = FavouritesViewModel(repository) // Initialize the ViewModel with the mock repository
    }

    // Unit test to verify the insertion of weather data into the repository
    @Test
    fun `test insert weather data`() = runTest {
        // Create a fake WeatherEntity object to simulate a weather data entry
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

        // Insert the fake weather data into the ViewModel
        viewModel.insertWeatherData(fakeWeatherEntity)

        // Collect all weather data from the repository using Flow
        val weatherData = repository.getAllWeatherData().first()

        // Assert that the inserted data matches the mock data
        assertEquals(listOf(fakeWeatherEntity), weatherData)
    }

    // Test to verify the deletion of weather data from the repository
    @Test
    fun test_delete_weather_data() = runTest {
        // Create and insert a fake WeatherEntity
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
        viewModel.insertWeatherData(fakeWeatherEntity)

        // Check that the data has been inserted
        val weather_data = viewModel.allWeatherData.first()
        assertEquals(1, weather_data.size)
        assertEquals("Cairo", weather_data[0].cityName)

        // Delete the weather data
        viewModel.deleteWeatherData(fakeWeatherEntity)

        // Verify that the weather data is now empty
        val updatedWeatherData = viewModel.allWeatherData.first()
        assertEquals(emptyList<WeatherEntity>(), updatedWeatherData)
    }

    // Test to verify retrieval of a specific city's weather data
    @Test
    fun test_get_weather_city() = runTest {
        // Create and insert a fake WeatherEntity for Alexandria
        val fakeWeatherEntity = WeatherEntity(
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
        viewModel.insertWeatherData(fakeWeatherEntity)

        // Retrieve the weather data for Alexandria
        val weatherCity = viewModel.getWeatherCity("Alexandria")

        // Assert that the retrieved data matches the inserted data
        assertEquals(fakeWeatherEntity, weatherCity)
    }

    // Test to verify the behavior when a city is not found
    @Test
    fun test_get_weather_city_not_found() = runTest {
        // Attempt to retrieve weather data for a city that hasn't been inserted
        val weatherCity = viewModel.getWeatherCity("Cairo")
        assertEquals(null, weatherCity) // Expect null since no data exists
    }

    // Test to verify retrieval of all weather data
    @Test
    fun test_getAllWeatherData() = runTest {
        // Create a list of fake WeatherEntity objects
        val mockWeatherFakeEntities = mutableListOf<WeatherEntity>()
        for (i in 1..10) {
            val mockWeatherEntity = WeatherEntity(
                cityName = "City $i",
                description = "Description for city $i",
                currentTemp = 20.0 + i,
                minTemp = 15.0 + i,
                maxTemp = 25.0 + i,
                pressure = 1010 + i,
                humidity = 50 + i,
                windSpeed = 5.0 + i,
                clouds = i,
                sunrise = 1600000000,
                sunset = 1600040000,
                date = "2024-09-28",
                latitude = 30.0444 + i * 0.01,
                longitude = 31.2357 + i * 0.01,
                lottie = 1
            )
            mockWeatherFakeEntities.add(mockWeatherEntity) // Add the mock entity to the list
            viewModel.insertWeatherData(mockWeatherEntity) // Insert it into the ViewModel
        }

        // Retrieve all weather data from the repository
        val retrievedWeatherData = repository.getAllWeatherData().first()

        // Assert that the retrieved weather data matches the mock data
        assertEquals(
            mockWeatherFakeEntities.sortedBy { it.cityName },
            retrievedWeatherData.sortedBy { it.cityName }
        )
    }

    // Tests for ViewModel-only operations to ensure encapsulation
    @Test
    fun `test insert weather data ViewModel Only`() = runTest {
        // Insert a fake WeatherEntity and verify the internal state of the ViewModel
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

        // Call the method to insert weather data
        viewModel.insertWeatherData(fakeWeatherEntity)

        // Collect all weather data from the ViewModel's internal state
        val weatherData = viewModel.allWeatherData.first()

        // Assert that the inserted data matches the mock data
        assertEquals(listOf(fakeWeatherEntity), weatherData)
    }

    @Test
    fun test_delete_weather_data_ViewModel_Only() = runTest {
        // Create and insert a fake WeatherEntity
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

        // Insert the weather data
        viewModel.insertWeatherData(fakeWeatherEntity)
        // Delete the weather data
        viewModel.deleteWeatherData(fakeWeatherEntity)

        // Collect the updated weather data from the ViewModel's internal state
        val weatherData = viewModel.allWeatherData.first()

        // Assert that the weather data is now empty after deletion
        assertEquals(emptyList<WeatherEntity>(), weatherData)
    }

    @Test
    fun test_get_weather_city_ViewModel_Only() = runTest {
        // Create and insert a fake WeatherEntity for Alexandria
        val fakeWeatherEntity = WeatherEntity(
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
        viewModel.insertWeatherData(fakeWeatherEntity)

        // Retrieve the weather data for Alexandria
        val weatherCity = viewModel.getWeatherCity("Alexandria")

        // Assert that the retrieved data matches the inserted data
        assertEquals(fakeWeatherEntity, weatherCity)
    }

    @Test
    fun test_get_weather_city_not_found_ViewModel_Only() = runTest {
        // Attempt to retrieve weather data for a city that hasn't been inserted
        val weatherCity = viewModel.getWeatherCity("Cairo")
        assertEquals(null, weatherCity) // Expect null since no data exists
    }

    @Test
    fun test_getAllWeatherData_ViewModel_Only() = runTest {
        // Create a list of fake WeatherEntity objects
        val mockWeatherFakeEntities = mutableListOf<WeatherEntity>()
        for (i in 1..10) {
            val mockWeatherEntity = WeatherEntity(
                cityName = "City $i",
                description = "Description for city $i",
                currentTemp = 20.0 + i,
                minTemp = 15.0 + i,
                maxTemp = 25.0 + i,
                pressure = 1010 + i,
                humidity = 50 + i,
                windSpeed = 5.0 + i,
                clouds = i,
                sunrise = 1600000000,
                sunset = 1600040000,
                date = "2024-09-28",
                latitude = 30.0444 + i * 0.01,
                longitude = 31.2357 + i * 0.01,
                lottie = 1
            )
            mockWeatherFakeEntities.add(mockWeatherEntity) // Add the mock entity to the list
            viewModel.insertWeatherData(mockWeatherEntity) // Insert it into the ViewModel
        }

        // Retrieve all weather data from the ViewModel's internal state
        val retrievedWeatherData = viewModel.allWeatherData.first()

        // Assert that the retrieved weather data matches the mock data
        assertEquals(
            mockWeatherFakeEntities.sortedBy { it.cityName },
            retrievedWeatherData.sortedBy { it.cityName }
        )
    }
}
