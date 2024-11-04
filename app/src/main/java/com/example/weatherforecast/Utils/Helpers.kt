package com.example.weatherforecast.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weatherforecast.R
import com.example.weatherforecast.Utils.Constants.CELSIUS_SHARED
import com.example.weatherforecast.Utils.Constants.FAHRENHEIT_SHARED
import com.example.weatherforecast.Utils.Constants.KELVIN_SHARED
import com.example.weatherforecast.Utils.Constants.METER_PER_SECOND
import com.example.weatherforecast.Utils.Constants.MILES_PER_HOUR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Helpers {

    /**
     * Converts temperature from Celsius to the specified unit.
     * @param tempInCelsius The temperature value in Celsius.
     * @param unit The target temperature unit (Celsius, Fahrenheit, or Kelvin).
     * @return The converted temperature value.
     */
    fun convertTempUnitFromCelsiusToAny(tempInCelsius: Double, unit: String): Double {
        return when (unit) {
            CELSIUS_SHARED -> tempInCelsius
            FAHRENHEIT_SHARED -> (tempInCelsius * 9 / 5) + 32
            KELVIN_SHARED -> tempInCelsius + 273.15
            else -> tempInCelsius // Default to Celsius if the unit is unrecognized
        }
    }

    /**
     * Retrieves the symbol for the specified temperature unit.
     * @param unit The temperature unit (Celsius, Fahrenheit, or Kelvin).
     * @return The symbol representing the temperature unit.
     */
    fun getTempUnitSymbol(unit: String): String {
        return when (unit) {
            CELSIUS_SHARED -> "C" // Celsius symbol
            FAHRENHEIT_SHARED -> "F" // Fahrenheit symbol
            KELVIN_SHARED -> "K" // Kelvin symbol
            else -> "C" // Default to Celsius if the unit is unrecognized
        }
    }

    /**
     * Retrieves the string resource ID for the wind speed unit symbol.
     * @param unit The wind speed unit (meters per second or miles per hour).
     * @return The string resource ID for the wind speed unit symbol.
     */
    fun getWindSpeedUnitSymbol(unit: String): Int {
        return when (unit) {
            METER_PER_SECOND -> R.string.m_s // String resource for meters per second
            MILES_PER_HOUR -> R.string.mph // String resource for miles per hour
            else -> R.string.m_s // Default to meters per second if the unit is unrecognized
        }
    }

    /**
     * Converts wind speed between different units.
     * @param speed The wind speed value to convert.
     * @param fromUnit The current wind speed unit.
     * @param neededUnit The target wind speed unit.
     * @return The converted wind speed value.
     */
    fun convertWindSpeed(speed: Double, fromUnit: String, neededUnit: String): Double {
        return when (neededUnit) {
            MILES_PER_HOUR -> if (fromUnit == METER_PER_SECOND) speed * 2.23694 else speed
            METER_PER_SECOND -> if (fromUnit == MILES_PER_HOUR) speed / 2.23694 else speed
            else -> speed // No conversion if the target unit is unrecognized
        }
    }

    /**
     * Formats a given Unix timestamp into a time string.
     * @param timestamp The Unix timestamp to format.
     * @return The formatted time string in "hh:mm a" format.
     */
    fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp * 1000))
    }

    /**
     * Gets the current date formatted as a string.
     * @return The formatted current date string in "dd MMMM yyyy" format.
     */
    fun getCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    /**
     * Extracts the hour from a given Unix timestamp.
     * @param unixTime The Unix timestamp to convert.
     * @return The hour of the day in 24-hour format.
     */
    fun getHourFromUnixTime(unixTime: Long): Int {
        val date = Date(unixTime * 1000L)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        return calendar.get(java.util.Calendar.HOUR_OF_DAY) // Get hour in 24-hour format
    }

    /**
     * Checks the availability of network connectivity.
     * @param context The application context.
     * @return True if network is available, otherwise false.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false // No active network
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // Connected via Wi-Fi
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // Connected via cellular
            else -> false // No valid connection
        }
    }

    /**
     * Formats a given time in milliseconds into a specific date and time string.
     * @param timeInMillis The time in milliseconds to format.
     * @return The formatted date and time string in "hh:mm a, MMM dd yyyy" format.
     */
    fun formatDatePlusYears(timeInMillis: Long): String {
        return SimpleDateFormat("hh:mm a, MMM dd yyyy", Locale.getDefault()).format(
            Date(timeInMillis)
        )
    }
}
