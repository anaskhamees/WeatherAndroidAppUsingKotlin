package com.example.weatherforecast.Features.Settings.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.Data.Repository.Repository
import com.example.weatherforecast.Features.Favourites.viewModel.FakeRepository
import com.example.weatherforecast.Utils.Constants.CELSIUS_SHARED
import com.example.weatherforecast.Utils.Constants.FAHRENHEIT_SHARED
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the [SettingsViewModel] class.
 * This test class utilizes a [FakeRepository] to isolate the view model from its dependencies,
 * allowing for testing of its functionality in a controlled environment.
 */
@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {
    // ViewModel under test
    private lateinit var viewModel: SettingsViewModel

    // Mock repository to simulate the data layer
    private lateinit var mockRepository: Repository

    /**
     * Sets up the test environment before each test case.
     * Initializes the mock repository and the view model.
     */
    @Before
    fun setup() {
        mockRepository = FakeRepository()
        viewModel = SettingsViewModel(mockRepository)
    }

    /**
     * Tests the get and set functionality of the temperature unit in the view model.
     * Asserts that the default temperature unit is Celsius and that it can be changed to Fahrenheit.
     */
    @Test
    fun test_get_and_set_temperature_unit() {
        assert(viewModel.getTemperatureUnit() == CELSIUS_SHARED)
        viewModel.setTemperatureUnit(FAHRENHEIT_SHARED)
        assert(viewModel.getTemperatureUnit() == FAHRENHEIT_SHARED)
    }

    /**
     * Tests the get and set functionality of the wind speed unit in the view model.
     * Asserts that the default wind speed unit is "km/h" and that it can be changed to "mph".
     */
    @Test
    fun test_get_and_set_wind_speed_unit() {
        assert(viewModel.getWindSpeedUnit() == "km/h")
        viewModel.setWindSpeedUnit("mph")
        assert(viewModel.getWindSpeedUnit() == "mph")
    }

    /**
     * Tests the get and set functionality of the notification preference in the view model.
     * Asserts that the default notification preference is true and that it can be changed to false.
     */
    @Test
    fun test_get_and_set_notification_preference() {
        assert(viewModel.getNotificationPreference() == true)
        viewModel.setNotificationPreference(false)
        assert(viewModel.getNotificationPreference() == false)
    }
}
