// Package declaration indicating the location of this class in the project structure.
package com.example.weatherforecast.Data.DataBase.sharedPrefrences

import android.content.SharedPreferences // Importing SharedPreferences for data storage.
import com.example.weatherforecast.Utils.Constants // Importing constants for key identifiers.
import com.example.weatherforecast.Utils.Constants.CELSIUS_SHARED // Constant for default temperature unit.
import com.example.weatherforecast.Utils.Constants.LATITUDE_SHARED // Constant for latitude key.
import com.example.weatherforecast.Utils.Constants.LONGITUDE_SHARED // Constant for longitude key.
import com.example.weatherforecast.Utils.Constants.NOTIFICATION_SHARED // Constant for notification preference key.
import com.example.weatherforecast.Utils.Constants.TEMP_UNIT_SHARED // Constant for temperature unit key.
import com.example.weatherforecast.Utils.Constants.WIND_SPEED_UNIT_SHARED // Constant for wind speed unit key.

// Class definition for SharedPrefsDataSourceImpl which implements the SharedPrefsDataSource interface.
class SharedPrefsDataSourceImpl(private val sharedPreferences: SharedPreferences) :
    SharedPrefsDataSource {

    // Function to get the temperature unit from SharedPreferences.
    override fun getTemperatureUnit(): String {
        // Retrieves the temperature unit; if not found, returns the default (Celsius).
        return sharedPreferences.getString(TEMP_UNIT_SHARED, CELSIUS_SHARED) ?: CELSIUS_SHARED
    }

    // Function to set the temperature unit in SharedPreferences.
    override fun setTemperatureUnit(unit: String) {
        // Edits the SharedPreferences to store the provided temperature unit.
        sharedPreferences.edit().putString(TEMP_UNIT_SHARED, unit).apply()
    }

    // Function to get the wind speed unit from SharedPreferences.
    override fun getWindSpeedUnit(): String {
        // Retrieves the wind speed unit; if not found, returns the default (meters per second).
        return sharedPreferences.getString(WIND_SPEED_UNIT_SHARED, Constants.METER_PER_SECOND)
            ?: Constants.METER_PER_SECOND
    }

    // Function to set the wind speed unit in SharedPreferences.
    override fun setWindSpeedUnit(unit: String) {
        // Edits the SharedPreferences to store the provided wind speed unit.
        sharedPreferences.edit().putString(WIND_SPEED_UNIT_SHARED, unit).apply()
    }

    // Function to save the user's location (latitude and longitude) in SharedPreferences.
    override fun saveLocation(latitude: Float, longitude: Float) {
        // Using 'with' to simplify editing SharedPreferences.
        with(sharedPreferences.edit()) {
            putFloat(LATITUDE_SHARED, latitude) // Stores the latitude.
            putFloat(LONGITUDE_SHARED, longitude) // Stores the longitude.
            apply() // Applies the changes asynchronously.
        }
    }

    // Function to retrieve the user's saved location as a Pair of Floats.
    override fun getLocation(): Pair<Float, Float>? {
        // Retrieves latitude and longitude from SharedPreferences.
        val latitude = sharedPreferences.getFloat(LATITUDE_SHARED, Float.NaN)
        val longitude = sharedPreferences.getFloat(LONGITUDE_SHARED, Float.NaN)

        // Checks if latitude and longitude are valid (not NaN).
        return if (!latitude.isNaN() && !longitude.isNaN()) {
            Pair(latitude, longitude) // Returns the coordinates as a Pair.
        } else {
            null // Returns null if the coordinates are not valid.
        }
    }

    // Function to get the user's notification preference from SharedPreferences.
    override fun getNotificationPreference(): Boolean {
        // Retrieves the notification preference; if not found, defaults to true (enabled).
        return sharedPreferences.getBoolean(NOTIFICATION_SHARED, true)
    }

    // Function to set the user's notification preference in SharedPreferences.
    override fun setNotificationPreference(enabled: Boolean) {
        // Edits the SharedPreferences to store the notification preference.
        return sharedPreferences.edit().putBoolean(NOTIFICATION_SHARED, enabled).apply()
    }

    // Generic function to retrieve a string value from SharedPreferences based on a key.
    override fun getString(key: String, defaultValue: String): String {
        // Retrieves the string value for the provided key; if not found, returns the default value.
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Generic function to store a string value in SharedPreferences based on a key.
    override fun putString(key: String, value: String) {
        // Edits the SharedPreferences to store the string value.
        sharedPreferences.edit().putString(key, value).apply()
    }
}
