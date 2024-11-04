package com.example.weatherforecast.Utils

object Constants {

    // API and Networking Constants
    /** Base URL for the OpenWeatherMap API. Used as the base for all API requests. */
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    /** API key for accessing OpenWeatherMap data. */
    const val API_KEY = "d8b0d405f9d41f5903ec35720dfdb84c"
    /** Unit system for temperature data. "metric" means Celsius. */
    const val UNITS = "metric"

    // Alarm and Notification Constants
    /** Identifier for the notification channel used for alarms. */
    const val ALARM_CHANNEL_ID = "ALARM_CHANNEL"

    // Shared Preferences Keys
    /** Key for storing the app language preference. */
    const val LANGUAGE_SHARED = "Language"
    /** Key for storing the preferred temperature unit (Celsius, Fahrenheit, or Kelvin). */
    const val TEMP_UNIT_SHARED = "TempUnit"
    /** Key for storing the preferred wind speed unit. */
    const val WIND_SPEED_UNIT_SHARED = "wind_speed_unit"
    /** Key for storing the last known latitude. */
    const val LATITUDE_SHARED = "latitude"
    /** Key for storing the last known longitude. */
    const val LONGITUDE_SHARED = "longitude"
    /** Key for enabling or disabling notifications. */
    const val NOTIFICATION_SHARED = "notifications_enabled"
    /** Name of the general shared preferences file for storing app settings. */
    const val SHARED_PREFS_NAME = "AppSettingPrefs"
    /** Key for saving the user’s favorite city for weather updates. */
    const val FAVOURITE_SHARED_CITY = "CITY_KEY"
    /** Name of shared preferences file for home screen settings. */
    const val HOME_SCREEN_SHARED_PREFS_NAME = "homeScreen"
    /** Name of shared preferences file used to store offline weather data. */
    const val OFFLINE_SHARED_PREFS_NAME = "OfflineHomeScreenData"

    // Formatting and Units
    /** Format string for displaying temperature with the unit symbol (e.g., "25°C"). */
    const val TEMPERATURE_FORMAT = "%.0f°%s"
    /** Wind speed unit in meters per second. */
    const val METER_PER_SECOND = "Meter/Second"
    /** Temperature unit option for Celsius. */
    const val CELSIUS_SHARED = "Celsius"
    /** Temperature unit option for Fahrenheit. */
    const val FAHRENHEIT_SHARED = "Fahrenheit"
    /** Temperature unit option for Kelvin. */
    const val KELVIN_SHARED = "Kelvin"
    /** Wind speed unit in miles per hour. */
    const val MILES_PER_HOUR = "Miles/Hour"

    // Language Options
    /** Language code for English. */
    const val ENGLISH_SHARED = "en"
    /** Language code for Arabic. */
    const val ARABIC_SHARED = "ar"

    // Location and Alarm-Related Constants
    /** Key for storing the device’s current latitude and longitude as a combined value. */
    const val CURRENT_DEVICE_LAT_LANG = "current_lan_lat"
    /** Key for managing the alarm ID to be dismissed. */
    const val ALARM_ID_TO_DISMISS = "ALARM_ID"
}
