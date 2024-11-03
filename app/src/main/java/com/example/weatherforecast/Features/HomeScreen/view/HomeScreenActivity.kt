package com.example.weatherforecast.Features.HomeScreen.view

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherforecast.Data.DataBase.local.favourites.LocalDataSourceImpl
import com.example.weatherforecast.Data.Network.RemoteDataSourceImpl
import com.example.weatherforecast.Data.DataBase.room.AppDatabase
import com.example.weatherforecast.Data.DataBase.sharedPrefrences.SharedPrefsDataSourceImpl
import com.example.weatherforecast.Data.Model.DailyForecastElement
import com.example.weatherforecast.Data.Model.Hourly
import com.example.weatherforecast.Data.Model.Weather
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.Data.Network.ApiClient
import com.example.weatherforecast.Data.Network.ApiState
import com.example.weatherforecast.Data.Repository.RepositoryImpl
import com.example.weatherforecast.databinding.ActivityHomeScreenBinding
import com.example.weatherforecast.Features.Alert.view.AlertActivity
import com.example.weatherforecast.Features.Favourites.view.FavouritesActivity
import com.example.weatherforecast.Features.Favourites.viewModel.FavouritesViewModel
import com.example.weatherforecast.Features.Favourites.viewModel.FavouritesViewModelFactory
import com.example.weatherforecast.Features.GoogleMaps.view.GoogleMapsActivity
import com.example.weatherforecast.Features.HomeScreen.viewModel.HomeViewModel
import com.example.weatherforecast.Features.HomeScreen.viewModel.HomeViewModelFactory
import com.example.weatherforecast.Features.Settings.view.SettingsActivity
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModel
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModelFactory
import com.example.weatherforecast.Services.pushNotifications.NotificationServices.notificationServices
import com.example.weatherforecast.Utils.Constants.ARABIC_SHARED
import com.example.weatherforecast.Utils.Constants.FAVOURITE_SHARED_CITY
import com.example.weatherforecast.Utils.Constants.HOME_SCREEN_SHARED_PREFS_NAME
import com.example.weatherforecast.Utils.Constants.LATITUDE_SHARED
import com.example.weatherforecast.Utils.Constants.LONGITUDE_SHARED
import com.example.weatherforecast.Utils.Constants.METER_PER_SECOND
import com.example.weatherforecast.Utils.Constants.OFFLINE_SHARED_PREFS_NAME
import com.example.weatherforecast.Utils.Constants.SHARED_PREFS_NAME
import com.example.weatherforecast.Utils.Constants.TEMPERATURE_FORMAT
import com.example.weatherforecast.Utils.Helpers.convertTempUnitFromCelsiusToAny
import com.example.weatherforecast.Utils.Helpers.convertWindSpeed
import com.example.weatherforecast.Utils.Helpers.date
import com.example.weatherforecast.Utils.Helpers.formatTime
import com.example.weatherforecast.Utils.Helpers.getTempUnitSymbol
import com.example.weatherforecast.Utils.Helpers.getWindSpeedUnitSymbol
import com.example.weatherforecast.Utils.Helpers.isNetworkAvailable
import com.example.weatherforecast.Utils.HomeScreenHelper.checkWeatherDescription
import com.example.weatherforecast.Utils.HomeScreenHelper.dynamicTextAnimation
import com.example.weatherforecast.Utils.HomeScreenHelper.slideInAndScaleView
import com.example.weatherforecast.Utils.HomeScreenHelper.slideInFromLeft
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.Locale

class HomeScreenActivity : AppCompatActivity() {
    /// Binding instance for accessing layout views.
    private lateinit var binding: ActivityHomeScreenBinding

    /// Adapter for displaying hourly weather data.
    private lateinit var hourlyAdapter: HourlyAdapter

    /// Adapter for displaying daily weather data.
    private lateinit var dailyAdapter: DailyAdapter

    /// Default city name to display.
    private var city: String = "Alex"

    /// Latitude passed from the intent.
    private var passedLat: Double = 0.0

    /// Longitude passed from the intent.
    private var passedLong: Double = 0.0

    /// Flag indicating if the view is for display only.
    private var isViewOnly: Boolean = false

    /// City name retrieved from SharedPreferences.
    private var cityName: String? = null

    /// ViewModel for accessing weather data.
    private val weatherViewModel: HomeViewModel by viewModels { HomeViewModelFactory(getRepository()) }

    /// ViewModel for accessing settings data.
    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(getRepository()) }

    /// ViewModel for accessing favourites data.
    private val favouritesViewModel: FavouritesViewModel by viewModels { FavouritesViewModelFactory(getRepository()) }

    /**
     * @brief Initializes the activity, views, adapters, and starts data fetch.
     * @param savedInstanceState Saved instance state bundle.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkRunningLanguage()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        setUpAdapters()
        gettingPassedKeysFromIntents()
        setCityNameBasedOnLatAndLong(passedLat, passedLong)
        fetchDataBasedOnLatAndLong()
        setUpCollector()
        savedLocationsDetails()
        swipeToRefresh()
        visibilityForViewerPage()
        if (!isNetworkAvailable(this)) {
            fetchWeatherDataFromSharedPreferences()
            showSnackBar()
        }
        notificationServices(this, passedLat, passedLong)
    }

    /**
     * @brief Sets up click listeners for the main action buttons in the view.
     */
    private fun setUpViews() {
        binding.btnMaps.setOnClickListener {
            if (isNetworkAvailable(this)) {
                startActivity(Intent(this, GoogleMapsActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    getString(com.example.weatherforecast.R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.btnFavourites.setOnClickListener {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
        binding.btnAlert.setOnClickListener {
            startActivity(Intent(this, AlertActivity::class.java))
        }
    }

    /**
     * @brief Initializes the adapters for displaying hourly and daily weather data.
     */
    private fun setUpAdapters() {
        dailyAdapter = DailyAdapter(settingsViewModel, lifecycleScope)
        hourlyAdapter = HourlyAdapter(settingsViewModel, lifecycleScope)

        binding.apply {
            rvHourlyDegrees.adapter = hourlyAdapter
            rvDetailedDays.adapter = dailyAdapter
        }
    }

    /**
     * @brief Retrieves latitude and longitude from the incoming intent.
     */
    private fun gettingPassedKeysFromIntents() {
        passedLat = intent.getDoubleExtra(LATITUDE_SHARED, 0.0)
        passedLong = intent.getDoubleExtra(LONGITUDE_SHARED, 0.0)
        isViewOnly = intent.getBooleanExtra("viewOnly", false)
        cityName = intent.getStringExtra(FAVOURITE_SHARED_CITY)
    }

    /**
     * @brief Sets the city name based on latitude and longitude using the Geocoder API.
     * @param lat Latitude of the location.
     * @param long Longitude of the location.
     */
    @Suppress("DEPRECATION")
    private fun setCityNameBasedOnLatAndLong(lat: Double, long: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, long, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // Safely handle potential null values for the admin area and country name
                val adminArea = address.adminArea?.takeIf { it.isNotBlank() } ?: ""
                val countryName = address.countryName?.takeIf { it.isNotBlank() } ?: ""

                // Get the language setting from SharedPreferences
                val language = settingsViewModel.getLanguage()

                // Determine the city name based on the language
                city = if (language == ARABIC_SHARED) { // Check if the language is Arabic
                    countryName // Only use the country name
                } else {
                    // Format the city string, including admin area and country name
                    listOf(adminArea, countryName)
                        .filter { it.isNotEmpty() }
                        .joinToString(", ")
                }

                if (city.isNotEmpty()) {
                    Log.e("HomeScreenActivity", "City: $city")
                }
            }
        } catch (e: IOException) {
            Log.e("HomeScreenActivity", "Geocoder service is unavailable: ${e.message}")
            // Handle the error (e.g., notify the user, use default values, etc.)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDataBasedOnLatAndLong() {
        lifecycleScope.launch {
            try {
                weatherViewModel.fetchCurrentWeatherDataByCoordinates(passedLat, passedLong)
                weatherViewModel.fetchHourlyWeatherByCoordinates(passedLat, passedLong)
                weatherViewModel.fetchDailyWeatherByCoordinates(passedLat, passedLong)
            } catch (e: Exception) {
                Toast.makeText(
                    this@HomeScreenActivity,
                    getString(com.example.weatherforecast.R.string.no_network_using_saved_data),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUpCollector() {
        gettingWeatherDataFromViewModel()
        gettingHourlyWeatherDataFromViewModel()
        gettingDailyWeatherDataFromViewModel()
    }

    private fun saveWeatherDataToSharedPreferences(weather: Weather) {
        val sharedPreferences =
            getSharedPreferences(HOME_SCREEN_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert Weather object to JSON string using Gson
        val gson = Gson()
        val weatherJson = gson.toJson(weather)

        editor.putString(OFFLINE_SHARED_PREFS_NAME, weatherJson)
        editor.apply() // Use apply() for asynchronous saving
    }

    private fun gettingWeatherDataFromViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherDataStateFlow.collect { apiState ->
                    when (apiState) {
                        is ApiState.Loading -> {
                            showLoading(true)
                            binding.cardDaysDetails.visibility = View.GONE
                            setVisibilityOfViewsOnScreen(true)
                        }

                        is ApiState.Success -> {
                            delay(600)
                            showLoading(false)
                            slideInAndScaleView(binding.cardDaysDetails)
                            setVisibilityOfViewsOnScreen(false)
                            val weatherData = apiState.data as Weather
                            launch {
                                updateUi(weatherData) // Update UI
                            }
                            saveWeatherDataToSharedPreferences(weatherData) // Save to SharedPreferences
                        }

                        is ApiState.Failure -> {
                            showLoading(false)
                            setVisibilityOfViewsOnScreen(false)
                            binding.rvHourlyDegrees.visibility = View.GONE
                            binding.rvDetailedDays.visibility = View.GONE
                            Log.e(
                                "WeatherError",
                                "Error retrieving weather data ${apiState.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchWeatherDataFromSharedPreferences() {
        val sharedPreferences =
            getSharedPreferences(HOME_SCREEN_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val weatherJson = sharedPreferences.getString(OFFLINE_SHARED_PREFS_NAME, null)

        if (weatherJson != null) {
            // Convert JSON string back to Weather object
            val gson = Gson()
            val weatherData: Weather = gson.fromJson(weatherJson, Weather::class.java)
            updateUi(weatherData) // Update UI with the saved weather data
        } else {
            // Handle case when no data is saved
            Log.e("WeatherError", "No weather data available in SharedPreferences.")
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale", "StringFormatMatches")
    private fun updateUi(weather: Weather) {
        val unit = settingsViewModel.getTemperatureUnit()
        val windSpeedUnit = settingsViewModel.getWindSpeedUnit()
        // Set background based on time of day
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)


        // Set Lottie based on weather
        val lottieAnimation = checkWeatherDescription(this, weather)
        binding.animWeather.setAnimation(lottieAnimation)
        binding.animWeather.playAnimation()

        Log.d("WeatherDebug", "Weather Main Data: ${weather.main}")

        // Debugging: Log the raw temperature values
        Log.d("WeatherDebug", "Current Temp: ${weather.main.temp}")
        Log.d("WeatherDebug", "Min Temp: ${weather.main.temp_min}")
        Log.d("WeatherDebug", "Max Temp: ${weather.main.temp_max}")

        // Convert temperatures
        val currentTemp = convertTempUnitFromCelsiusToAny(weather.main.temp, unit)
        val minTemp = convertTempUnitFromCelsiusToAny(weather.main.temp_min, unit)
        val maxTemp = convertTempUnitFromCelsiusToAny(weather.main.temp_max, unit)

        // Debugging: Log converted temperature values
        Log.d("WeatherDebug", "Converted Current Temp: $currentTemp")
        Log.d("WeatherDebug", "Converted Min Temp: $minTemp")
        Log.d("WeatherDebug", "Converted Max Temp: $maxTemp")

        // Set text on TextViews
        binding.tvCurrentDegree.text = String.format(TEMPERATURE_FORMAT, currentTemp, getTempUnitSymbol(unit))
        binding.tvTempMin.text = String.format(TEMPERATURE_FORMAT, minTemp, getTempUnitSymbol(unit))
        binding.tvTempMax.text = String.format(TEMPERATURE_FORMAT, maxTemp, getTempUnitSymbol(unit))

        // Update weather details
        val cityName = city.trim()
        val words = cityName.split(" ")
        binding.tvCityName.text = if (words.size > 2) {
            val firstLine = words.take(2).joinToString(" ") // First two words
            val secondLine = words.drop(2).joinToString(" ") // Remaining words
            "$firstLine\n$secondLine"
        } else {
            cityName // If it's 2 words or less, keep it as is
        }
        binding.tvWeatherStatus.text = weather.weather[0].description
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        binding.tvDate.text = date()
        binding.tvPressureValue.text = getString(com.example.weatherforecast.R.string.hpa, weather.main.pressure)
        binding.tvHumidityValue.text = "${weather.main.humidity} %"
        val windSpeed = convertWindSpeed(weather.wind.speed, METER_PER_SECOND, windSpeedUnit)
        binding.tvWindValue.text = String.format(
            Locale.getDefault(),
            "%.0f %s",
            windSpeed,
            getString(getWindSpeedUnitSymbol(windSpeedUnit))
        )

        // Additional info
        binding.tvCloudValue.text = "${weather.clouds.all} %"
        binding.tvSunriseValue.text = formatTime(weather.sys.sunrise)
        binding.tvSunsetValue.text = formatTime(weather.sys.sunset)

        // Save data to local database when Save button is clicked
        onSaveButtonClick(
            currentTemp,
            minTemp,
            maxTemp,
            weather,
            windSpeed,
            passedLat,
            passedLong,
            lottieAnimation
        )
    }

// Handles the "Save" button click to save current weather data into the database
    private fun onSaveButtonClick(
        currentTemp: Double,
        minTemp: Double,
        maxTemp: Double,
        weather: Weather,
        windSpeed: Double,
        lat: Double,
        long: Double,
        lottie: Int
    ) {
        binding.btnSave.setOnClickListener {
            // Create a WeatherEntity object with current UI data for database storage
            val weatherEntity = WeatherEntity(
                cityName = city,
                description = binding.tvWeatherStatus.text.toString(),
                currentTemp = currentTemp,
                minTemp = minTemp,
                maxTemp = maxTemp,
                pressure = weather.main.pressure,
                humidity = weather.main.humidity,
                windSpeed = windSpeed,
                clouds = weather.clouds.all.toInt(),
                sunrise = weather.sys.sunrise,
                sunset = weather.sys.sunset,
                date = binding.tvDate.text.toString(),
                latitude = lat,
                longitude = long,
                lottie = lottie
            )
            // Launch a coroutine to insert the WeatherEntity into the database using ViewModel
            lifecycleScope.launch {
                favouritesViewModel.insertWeatherData(weatherEntity)
                // Show a confirmation message on successful save
                Toast.makeText(
                    this@HomeScreenActivity,
                    getString(com.example.weatherforecast.R.string.location_saved_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
// Collects hourly weather data from ViewModel and updates the UI
    private fun gettingHourlyWeatherDataFromViewModel() {
        lifecycleScope.launch {
            // Collects data only while the lifecycle is in the STARTED state
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.hourlyForecastDataStateFlow.collect { apiState ->
                    when (apiState) {
                        is ApiState.Loading -> {
                            // Handle loading state
                        }

                        is ApiState.Success -> {
                            // Get the first 9 items from hourly forecast data and update adapter
                            val hourlyList = (apiState.data as Hourly).list.take(9)
                            hourlyAdapter.submitList(hourlyList)
                        }

                        is ApiState.Failure -> {
                            // Log the error message if data retrieval fails
                            Log.e(
                                "WeatherError",
                                "Error retrieving hourly forecast data ${apiState.message}"
                            )
                        }
                    }
                }
            }
        }
    }
// Collects daily weather data from ViewModel and updates the UI
    private fun gettingDailyWeatherDataFromViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.dailyForecastDataStateFlow.collect { apiState ->
                    when (apiState) {
                        is ApiState.Loading -> {
                            // Hide daily weather details during loading
                            binding.rvDetailedDays.visibility = View.GONE
                            binding.cardDaysDetails.visibility = View.GONE
                        }

                        is ApiState.Success -> {
                            // Delay to allow for smooth UI updates, then show daily weather details
                            delay(600)
                            binding.rvDetailedDays.visibility = View.VISIBLE
                            binding.cardDaysDetails.visibility = View.VISIBLE
                            // Update the adapter with a filtered list of daily forecast elements
                            val dailyList =
                                (apiState.data as List<*>).filterIsInstance<DailyForecastElement>()
                            dailyAdapter.submitList(dailyList)
                        }

                        is ApiState.Failure -> {
                            // Hide daily weather details and log the error
                            binding.rvDetailedDays.visibility = View.GONE
                            binding.cardDaysDetails.visibility = View.GONE
                            Log.e(
                                "WeatherError",
                                "Error retrieving daily forecast data ${apiState.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    // Loads saved location details from the database or network
    private fun savedLocationsDetails() {
        if (cityName != null) {
            lifecycleScope.launch {
                if (isNetworkAvailable(this@HomeScreenActivity)) {
                    // If network is available, fetch saved data from the database
                    val weatherEntity = favouritesViewModel.getWeatherCity(cityName!!)
                    if (weatherEntity != null) {
                        disableViewsForFavouritesViewer()
                        fetchDataFromDataBaseOrFromRemoteIfNetworkAvailable(weatherEntity)
                    }
                } else {
                    // If offline, load saved data for offline viewing
                    val weatherEntity = favouritesViewModel.getWeatherCity(cityName!!)
                    if (weatherEntity != null) {
                        // Load the offline weather data (you might want to update UI accordingly)
                        loadOfflineData(weatherEntity)
                        Log.e(
                            "HomeScreenActivity",
                            "Loaded offline data for ${weatherEntity.cityName}"
                        )
                    }
                }
            }
        }
    }

    // Hides specific UI elements (Icons) when in "Favorites" viewing mode
    private fun disableViewsForFavouritesViewer() {
        binding.btnMaps.visibility = View.GONE
        binding.btnFavourites.visibility = View.GONE
        binding.btnAlert.visibility = View.GONE
        binding.btnSave.visibility = View.GONE
        binding.btnSettings.visibility = View.GONE
    }

    // Fetches weather data based on network availability and updates UI accordingly
    private fun fetchDataFromDataBaseOrFromRemoteIfNetworkAvailable(weatherEntity: WeatherEntity) {
        // Fetch current weather data based on coordinates
        weatherViewModel.fetchCurrentWeatherDataByCoordinates(
            weatherEntity.latitude,
            weatherEntity.longitude
        )
        // Sets city name using latitude and longitude
        setCityNameBasedOnLatAndLong(weatherEntity.latitude, weatherEntity.longitude)
        binding.swipeToRefresh.setOnRefreshListener {
            // Fetch current weather data based on coordinates
            weatherViewModel.fetchCurrentWeatherDataByCoordinates(
                weatherEntity.latitude,
                weatherEntity.longitude
            )
            binding.swipeToRefresh.isRefreshing = false
        }
        Log.e("HomeScreenActivity", "Loaded data from database")
    }

    // Loads offline weather data and displays it on the UI
    //@SuppressLint("SetTextI18n", "DefaultLocale")
    private fun loadOfflineData(weatherEntity: WeatherEntity) {
        // Get temperature unit and wind speed unit from settings
        val unit = settingsViewModel.getTemperatureUnit()
        val windSpeedUnit = settingsViewModel.getWindSpeedUnit()

        // Convert and display temperature values
        val currentTemp = convertTempUnitFromCelsiusToAny(weatherEntity.currentTemp, unit)
        binding.tvCurrentDegree.text =
            String.format(TEMPERATURE_FORMAT, currentTemp, getTempUnitSymbol(unit))
        val minTemp = convertTempUnitFromCelsiusToAny(weatherEntity.minTemp, unit)
        binding.tvTempMin.text = String.format(TEMPERATURE_FORMAT, minTemp, getTempUnitSymbol(unit))
        val maxTemp = convertTempUnitFromCelsiusToAny(weatherEntity.maxTemp, unit)
        binding.tvTempMax.text = String.format(TEMPERATURE_FORMAT, maxTemp, getTempUnitSymbol(unit))

        // Set other weather details on the UI
        binding.tvCityName.text = weatherEntity.cityName
        binding.tvWeatherStatus.text = weatherEntity.description
        binding.tvDate.text = weatherEntity.date
        binding.tvPressureValue.text = "${weatherEntity.pressure} hpa"
        binding.tvHumidityValue.text = "${weatherEntity.humidity} %"

        // Convert and display wind speed
        val windSpeed = convertWindSpeed(weatherEntity.windSpeed, METER_PER_SECOND, windSpeedUnit)
        binding.tvWindValue.text = String.format(
            Locale.getDefault(),
            "%.0f %s",
            windSpeed,
            getString(getWindSpeedUnitSymbol(windSpeedUnit))
        )

        // Additional weather info
        binding.tvCloudValue.text = "${weatherEntity.clouds} %"
        binding.tvSunriseValue.text = formatTime(weatherEntity.sunrise)
        binding.tvSunsetValue.text = formatTime(weatherEntity.sunset)

        // Set Lottie animation based on weather type
        binding.animWeather.setAnimation(weatherEntity.lottie)
        binding.animWeather.playAnimation()

        // Hide buttons and disable swipe-to-refresh
        disableViewsForFavouritesViewer()
        binding.swipeToRefresh.isActivated = false
    }

    // Handles swipe-to-refresh logic to reload data
    @RequiresApi(Build.VERSION_CODES.O)
    private fun swipeToRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            if (isNetworkAvailable(this)) {
                fetchDataBasedOnLatAndLong()
                binding.swipeToRefresh.isRefreshing = false
            } else {
                // Show no-network message if offline
                showSnackBar()
                binding.swipeToRefresh.isRefreshing = false
            }
        }
    }

    // Checks if network is available and fetches data on activity resume
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        // Load fresh data on resume
        fetchDataBasedOnLatAndLong()
        setUpAdapters()
        if (!isNetworkAvailable(this)) {
            // Show a message if there’s no network
            showSnackBar()
        }
    }

    private fun getRepository() = RepositoryImpl(
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

    // Sets the visibility of different views depending on loading state
    private fun setVisibilityOfViewsOnScreen(isLoading: Boolean) {
        if (isLoading) {
            // Hide main content while loading
            binding.tvCityName.visibility = View.GONE
            binding.tvCurrentDegree.visibility = View.GONE
            binding.tvWeatherStatus.visibility = View.GONE
            binding.tvTempMin.visibility = View.GONE
            binding.tvTempMax.visibility = View.GONE
            binding.cardWeatherDetails.visibility = View.GONE
            binding.rvHourlyDegrees.visibility = View.GONE
            binding.rvDetailedDays.visibility = View.GONE
            binding.tvDate.visibility = View.GONE
        } else {
            // Show and animate views once loading is complete
            slideInFromLeft(binding.tvCityName)
            slideInFromLeft(binding.tvDate)
            dynamicTextAnimation(binding.tvCurrentDegree)
            slideInAndScaleView(binding.tvWeatherStatus)
            dynamicTextAnimation(binding.tvTempMin)
            dynamicTextAnimation(binding.tvTempMax)
            slideInAndScaleView(binding.cardWeatherDetails)
            slideInAndScaleView(binding.rvHourlyDegrees)
            slideInAndScaleView(binding.rvDetailedDays)
        }
    }

    // Controls the visibility of the loading spinner based on the loading state
    private fun showLoading(isLoading: Boolean) {
        // If `isLoading` is true, show the progress spinner; otherwise, hide it
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Adjusts visibility of certain UI elements when in "View-Only" mode
    private fun visibilityForViewerPage() {
        // Check if the app is in "View-Only" mode
        if (isViewOnly) {
            // Hide certain buttons that are not relevant in View-Only mode
            binding.btnMaps.visibility = View.GONE
            binding.btnFavourites.visibility = View.GONE
            binding.btnAlert.visibility = View.GONE
            // Ensure the "Save" button is visible to allow saving current data
            binding.btnSave.visibility = View.VISIBLE
        }
    }

    // Displays a snackbar message informing the user that data is loaded from the cache due to lack of network
    private fun showSnackBar() {
        // Create a Snackbar with a message indicating no network connection, showing cached data
        val snackBar = Snackbar.make(
            findViewById(R.id.content), // Root view for positioning the Snackbar
            getString(com.example.weatherforecast.R.string.no_network_connection_data_loaded_from_cache),
            Snackbar.LENGTH_LONG // Duration of the Snackbar message
        )
        // Show the Snackbar message on the screen
        snackBar.show()
    }

    // Sets the app’s language based on user preference stored in settings
    private fun checkRunningLanguage() {
        // Retrieve the user's preferred language from the settings
        val language = settingsViewModel.getLanguage()
        // Create a Locale instance for the selected language
        val locale = Locale(language)
        // Set the app's default locale to the chosen language
        Locale.setDefault(locale)
        // Create a new configuration for the selected language
        val config = Configuration()
        config.setLocale(locale)

        // Apply the language configuration to the application context
        applicationContext.createConfigurationContext(config)
        applicationContext.resources.updateConfiguration(config, resources.displayMetrics)
        // Apply the configuration for the current activity context
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}