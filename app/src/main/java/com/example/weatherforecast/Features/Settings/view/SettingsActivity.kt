package com.example.weatherforecast.Features.Settings.view

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.DataBase.local.favourites.LocalDataSourceImpl
import com.example.weatherforecast.Data.Network.RemoteDataSourceImpl
import com.example.weatherforecast.Data.DataBase.room.AppDatabase
import com.example.weatherforecast.Data.DataBase.sharedPrefrences.SharedPrefsDataSourceImpl
import com.example.weatherforecast.Data.Network.ApiClient
import com.example.weatherforecast.Data.Repository.RepositoryImpl
import com.example.weatherforecast.databinding.ActivitySettingsBinding
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModel
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModelFactory
import com.example.weatherforecast.Features.splash.SplashActivity
import com.example.weatherforecast.Utils.Constants.ARABIC_SHARED
import com.example.weatherforecast.Utils.Constants.CELSIUS_SHARED
import com.example.weatherforecast.Utils.Constants.ENGLISH_SHARED
import com.example.weatherforecast.Utils.Constants.FAHRENHEIT_SHARED
import com.example.weatherforecast.Utils.Constants.KELVIN_SHARED
import com.example.weatherforecast.Utils.Constants.METER_PER_SECOND
import com.example.weatherforecast.Utils.Constants.MILES_PER_HOUR
import com.example.weatherforecast.Utils.Constants.SHARED_PREFS_NAME
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(
                    apiService = ApiClient.retrofit,
                    sharedPrefsDataSource = SharedPrefsDataSourceImpl(
                        this.getSharedPreferences(
                            SHARED_PREFS_NAME,
                            MODE_PRIVATE
                        )
                    )
                ),
                sharedPrefsDataSource = SharedPrefsDataSourceImpl(
                    this.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                ),
                localDataSource = LocalDataSourceImpl(AppDatabase.getDatabase(this).weatherDao())
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        checkRunningLanguage()
        onBackButtonClick()
        onTemperatureUnitSelection()
        onWindSpeedUnitSelection()
        setLanguageSelection()

        val savedUnit = settingsViewModel.getTemperatureUnit()
        when (savedUnit) {
            CELSIUS_SHARED -> binding.radioCelsius.isChecked = true
            FAHRENHEIT_SHARED -> binding.radioFahrenheit.isChecked = true
            KELVIN_SHARED -> binding.radioKelvin.isChecked = true
        }

        val savedSpeedUnit = settingsViewModel.getWindSpeedUnit()
        when (savedSpeedUnit) {
            METER_PER_SECOND -> binding.radioMeterPerSecond.isChecked = true
            MILES_PER_HOUR -> binding.radioMilesPerHour.isChecked = true
        }

        onTurnOnNotificationsClick()
        onTurnOffNotificationsClick()

        val notificationsEnabled = settingsViewModel.getNotificationPreference()
        when (notificationsEnabled) {
            true -> binding.radioOnNotification.isChecked = true
            false -> binding.radioOffNotification.isChecked = true
        }
    }

    private fun setLanguageSelection() {
        val savedLanguage = settingsViewModel.getLanguage()
        when (savedLanguage) {
            ENGLISH_SHARED -> binding.radioEnglish.isChecked = true
            ARABIC_SHARED -> binding.radioArabic.isChecked = true
        }

        binding.radioEnglish.setOnClickListener {
            showLanguageChangeDialog(ENGLISH_SHARED)
        }
        binding.radioArabic.setOnClickListener {
            showLanguageChangeDialog(ARABIC_SHARED)
        }
    }

    private fun showLanguageChangeDialog(languageCode: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.language_change))
            .setMessage(getString(R.string.changing_the_language_will_exit_the_app_please_reopen_the_app_to_see_the_changes))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                settingsViewModel.setLanguage(languageCode)
                switchLanguage(languageCode)

                finishAffinity()

                val intent = Intent(applicationContext, SplashActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null) // User canceled, do nothing
            .create()

        dialog.setOnShowListener {
            val buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val buttonCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Change text color
            buttonOk.setTextColor(
                resources.getColor(
                    R.color.buttons_,
                    null
                )
            ) // Change to your desired color
            buttonCancel.setTextColor(
                resources.getColor(
                    R.color.buttons_,
                    null
                )
            ) // Change to your desired color
        }
        dialog.show()
    }

    private fun switchLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    private fun onTurnOffNotificationsClick() {
        binding.radioOffNotification.setOnClickListener {
            handleNotificationPreference(false)
        }
    }

    private fun onTurnOnNotificationsClick() {
        binding.radioOnNotification.setOnClickListener {
            handleNotificationPreference(true)
        }
    }

    private fun handleNotificationPreference(enable: Boolean) {
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            }
        } else {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
            startActivity(intent)
        }
        settingsViewModel.setNotificationPreference(enable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "Permission Granted")
                handleNotificationPreference(true)
            } else {
                Log.e("TAG", "Permission Denied")
                handleNotificationPreference(false)
            }
        }
    }


    private fun onWindSpeedUnitSelection() {
        binding.radioMeterPerSecond.setOnClickListener {
            settingsViewModel.setWindSpeedUnit(METER_PER_SECOND)
        }
        binding.radioMilesPerHour.setOnClickListener {
            settingsViewModel.setWindSpeedUnit(MILES_PER_HOUR)
        }
    }


    private fun onTemperatureUnitSelection() {
        binding.radioKelvin.setOnClickListener {
            settingsViewModel.setTemperatureUnit(KELVIN_SHARED)
        }
        binding.radioCelsius.setOnClickListener {
            settingsViewModel.setTemperatureUnit(CELSIUS_SHARED)
        }
        binding.radioFahrenheit.setOnClickListener {
            settingsViewModel.setTemperatureUnit(FAHRENHEIT_SHARED)
        }
    }

    private fun onBackButtonClick() {
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val areSystemNotificationsEnabled =
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        if (areSystemNotificationsEnabled) {
            binding.radioOnNotification.isChecked = true
            settingsViewModel.setNotificationPreference(true)
        } else {
            binding.radioOffNotification.isChecked = true
            settingsViewModel.setNotificationPreference(false)
        }
    }
}