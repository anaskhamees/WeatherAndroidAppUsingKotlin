package com.example.weatherforecast.Services.alertAlarm

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.Network.RemoteDataSourceImpl
import com.example.weatherforecast.Data.DataBase.sharedPrefrences.SharedPrefsDataSourceImpl
import com.example.weatherforecast.Data.Network.ApiClient
import com.example.weatherforecast.Utils.Constants.HOME_SCREEN_SHARED_PREFS_NAME
import com.example.weatherforecast.Utils.NotificationHelper
import com.example.weatherforecast.Utils.SharedPrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherAlarmWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @SuppressLint("StringFormatMatches")
    override suspend fun doWork(): Result {
        val (lat, lon) = SharedPrefsHelper.getLatLonBasedOnLocation(context)

        return withContext(Dispatchers.IO) {
            try {
                val weatherRepository = RemoteDataSourceImpl(
                    apiService = ApiClient.retrofit,
                    sharedPrefsDataSource = SharedPrefsDataSourceImpl(
                        context.getSharedPreferences(
                            HOME_SCREEN_SHARED_PREFS_NAME,
                            Context.MODE_PRIVATE
                        )
                    )
                )

                // Collect the weather data from the repository
                weatherRepository.fetchCurrentWeather(lat, lon).collect { weatherData ->
                    val currentTemp = weatherData.main.temp.toInt()
                    // Show a notification with the fetched weather data
                    NotificationHelper.showNotification(
                        context,
                        "Weather Update -> ${weatherData.weather[0].description}",
                        context.getString(R.string.current_temperature_c, currentTemp),
                        R.drawable.app_logo
                    )
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}