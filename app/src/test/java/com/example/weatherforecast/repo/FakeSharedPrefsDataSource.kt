package com.example.weatherforecast.repo

import com.example.weatherforecast.Data.DataBase.sharedPrefrences.SharedPrefsDataSource

/**
 * A fake implementation of the SharedPrefsDataSource interface for testing purposes.
 * This class simulates shared preferences functionality and provides a structure for
 * unit testing components that depend on shared preferences.
 */
class FakeSharedPrefsDataSource : SharedPrefsDataSource {

    /**
     * Retrieves the temperature unit from shared preferences.
     * This method is not yet implemented.
     *
     * @return The temperature unit as a String.
     */
    override fun getTemperatureUnit(): String {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Sets the temperature unit in shared preferences.
     * This method is not yet implemented.
     *
     * @param unit The temperature unit to be set.
     */
    override fun setTemperatureUnit(unit: String) {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Retrieves the wind speed unit from shared preferences.
     * This method is not yet implemented.
     *
     * @return The wind speed unit as a String.
     */
    override fun getWindSpeedUnit(): String {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Sets the wind speed unit in shared preferences.
     * This method is not yet implemented.
     *
     * @param unit The wind speed unit to be set.
     */
    override fun setWindSpeedUnit(unit: String) {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Saves the user's location (latitude and longitude) in shared preferences.
     * This method is not yet implemented.
     *
     * @param latitude The latitude to save.
     * @param longitude The longitude to save.
     */
    override fun saveLocation(latitude: Float, longitude: Float) {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Retrieves the saved location from shared preferences.
     * This method is not yet implemented.
     *
     * @return A Pair of Floats representing latitude and longitude, or null if not set.
     */
    override fun getLocation(): Pair<Float, Float>? {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Retrieves the user's notification preference from shared preferences.
     * This method is not yet implemented.
     *
     * @return A Boolean indicating whether notifications are enabled.
     */
    override fun getNotificationPreference(): Boolean {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Sets the user's notification preference in shared preferences.
     * This method is not yet implemented.
     *
     * @param enabled A Boolean indicating whether to enable notifications.
     */
    override fun setNotificationPreference(enabled: Boolean) {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Retrieves a string value from shared preferences based on the provided key.
     * If the key is not found, returns the provided default value.
     * This method is not yet implemented.
     *
     * @param key The key to look for in shared preferences.
     * @param defaultValue The value to return if the key is not found.
     * @return The string value associated with the key, or the default value.
     */
    override fun getString(key: String, defaultValue: String): String {
        TODO("Not yet implemented") // Placeholder for future implementation
    }

    /**
     * Stores a string value in shared preferences with the specified key.
     * This method is not yet implemented.
     *
     * @param key The key under which to store the value.
     * @param value The string value to store.
     */
    override fun putString(key: String, value: String) {
        TODO("Not yet implemented") // Placeholder for future implementation
    }
}
