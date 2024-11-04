package com.example.weatherforecast.Utils

import android.content.Context
import android.location.LocationManager
import com.example.weatherforecast.Utils.Constants.CURRENT_DEVICE_LAT_LANG
import com.example.weatherforecast.Utils.Constants.HOME_SCREEN_SHARED_PREFS_NAME
import com.example.weatherforecast.Utils.Constants.LATITUDE_SHARED
import com.example.weatherforecast.Utils.Constants.LONGITUDE_SHARED

/**
 * A utility object for managing Shared Preferences related to location data in the application.
 */
object SharedPrefsHelper {

    /**
     * Retrieves the current latitude and longitude based on the device's location settings.
     *
     * @param context The context used to access system services and shared preferences.
     * @return A Pair containing the latitude and longitude as Doubles.
     */
    fun getLatLonBasedOnLocation(context: Context): Pair<Double, Double> {
        // Check if location services are enabled on the device
        return if (isLocationEnabled(context)) {
            // If location services are enabled, get coordinates from CURRENT_DEVICE_LAT_LANG preferences
            getLatLonFromPrefs(context, CURRENT_DEVICE_LAT_LANG)
        } else {
            // If location services are not enabled, fall back to HOME_SCREEN_SHARED_PREFS_NAME preferences
            getLatLonFromPrefs(context, HOME_SCREEN_SHARED_PREFS_NAME)
        }
    }

    /**
     * Checks if the location services (GPS or network) are enabled on the device.
     *
     * @param context The context used to access the LocationManager.
     * @return True if either GPS or network location providers are enabled; otherwise, false.
     */
    private fun isLocationEnabled(context: Context): Boolean {
        // Obtain the LocationManager from the system services
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Check if either GPS_PROVIDER or NETWORK_PROVIDER is enabled
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Retrieves the latitude and longitude from the specified shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @param prefsName The name of the shared preferences from which to retrieve the coordinates.
     * @return A Pair containing the latitude and longitude as Doubles.
     */
    fun getLatLonFromPrefs(context: Context, prefsName: String): Pair<Double, Double> {
        // Access the shared preferences with the given name in private mode
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        // Retrieve the latitude and longitude values stored in shared preferences, defaulting to 0.0 if not found
        val lat = sharedPreferences.getFloat(LATITUDE_SHARED, 0.0f).toDouble()
        val lon = sharedPreferences.getFloat(LONGITUDE_SHARED, 0.0f).toDouble()
        // Return the latitude and longitude as a Pair
        return lat to lon
    }
}
