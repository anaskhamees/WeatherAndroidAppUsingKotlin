package com.example.weatherforecast.Features.HomeScreen.view

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.Model.DailyForecastElement
import com.example.weatherforecast.databinding.ItemDailyBinding
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModel
import com.example.weatherforecast.Utils.Constants.TEMPERATURE_FORMAT
import com.example.weatherforecast.Utils.Helpers.convertTempUnitFromCelsiusToAny
import com.example.weatherforecast.Utils.Helpers.getTempUnitSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Adapter class for displaying daily weather forecast in a RecyclerView.
 * It takes a SettingsViewModel to get temperature units and a lifecycle scope to handle coroutines.
 *
 * @property settingsViewModel The SettingsViewModel instance for accessing user settings.
 * @property lifecycleScope The CoroutineScope tied to the lifecycle for launching coroutines.
 */
class DailyAdapter(
    private val settingsViewModel: SettingsViewModel,
    private val lifecycleScope: CoroutineScope
) : ListAdapter<DailyForecastElement, DailyAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    /**
     * Inflates the item layout and creates a ViewHolder for daily weather items.
     *
     * @param parent The parent ViewGroup in which the ViewHolder is created.
     * @param viewType The type of view for the item.
     * @return A new instance of DailyWeatherViewHolder.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyAdapter.DailyWeatherViewHolder {
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyWeatherViewHolder(binding)
    }

    /**
     * Binds the data to the ViewHolder and applies an animation to each item.
     *
     * @param holder The ViewHolder instance for the current item.
     * @param position The position of the item in the list.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailyAdapter.DailyWeatherViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_bottom)
        holder.bind(getItem(position))
        holder.itemView.startAnimation(animation)
    }

    /**
     * ViewHolder class for displaying the daily weather data within each RecyclerView item.
     *
     * @property binding The ItemDailyBinding instance for accessing the layout views.
     */
    inner class DailyWeatherViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the daily weather data to the item views.
         *
         * @param dailyWeather The DailyForecastElement instance containing weather details for the day.
         */
        @RequiresApi(Build.VERSION_CODES.O)
       // @SuppressLint("DefaultLocale")
        fun bind(dailyWeather: DailyForecastElement) {
            lifecycleScope.launch(Dispatchers.Main) {
                // Fetch temperature unit preference from settings
                val unit = settingsViewModel.getTemperatureUnit()

                // Convert timestamp to LocalDate based on system's timezone
                val date = Instant.ofEpochSecond(dailyWeather.dt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Determine the display string for the date (Today, Tomorrow, or day of the week)
                val today = LocalDate.now()
                val dayString = when (date) {
                    today -> getString(itemView.context, R.string.today)
                    today.plusDays(1) -> getString(itemView.context, R.string.tomorrow)
                    else -> date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
                }
                binding.tvDayDays.text = dayString

                // Convert max and min temperatures to the user-selected unit and format them
                val maxTemp = convertTempUnitFromCelsiusToAny(dailyWeather.main.temp_max, unit)
                val minTemp = convertTempUnitFromCelsiusToAny(dailyWeather.main.temp_min, unit)
                binding.tvHighDegree.text = String.format("%.0f", maxTemp, getTempUnitSymbol(unit))
                binding.tvLowDegree.text = String.format(TEMPERATURE_FORMAT, minTemp, getTempUnitSymbol(unit))

                // Set weather condition text with capitalization for each word
                binding.tvWeatherCondition.text = dailyWeather.weather[0].description
                    .split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

                // Get appropriate icon for the weather condition and set it in ImageView
                val iconCode = dailyWeather.weather[0].icon
                binding.ivIconDays.setImageResource(getWeatherDescriptionIcon(iconCode))
            }
        }
    }

    /**
     * Retrieves the corresponding icon resource ID for a given weather icon code.
     *
     * @param iconCode The icon code representing a specific weather condition.
     * @return The resource ID of the corresponding weather icon drawable.
     */
    private fun getWeatherDescriptionIcon(iconCode: String): Int {
        return when (iconCode) {
            "01d", "01n" -> R.drawable.ic_clear_sky
            "02d", "02n" -> R.drawable.ic_few_cloud
            "03d", "03n" -> R.drawable.ic_scattered_clouds
            "04d", "04n" -> R.drawable.ic_broken_clouds
            "09d", "09n" -> R.drawable.ic_shower_rain
            "10d", "10n" -> R.drawable.ic_rain
            "11d", "11n" -> R.drawable.ic_thunderstorm
            "13d", "13n" -> R.drawable.ic_snow
            "50d", "50n" -> R.drawable.ic_mist
            else -> R.drawable.ic_clear_sky
        }
    }

    /**
     * Callback for calculating the difference between two DailyForecastElement items.
     * Used to optimize updates to the RecyclerView.
     */
    class DailyWeatherDiffCallback : DiffUtil.ItemCallback<DailyForecastElement>() {

        /**
         * Checks if two items represent the same date.
         *
         * @param oldItem The previous DailyForecastElement.
         * @param newItem The new DailyForecastElement.
         * @return `true` if the items represent the same timestamp; otherwise, `false`.
         */
        override fun areItemsTheSame(
            oldItem: DailyForecastElement,
            newItem: DailyForecastElement
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        /**
         * Checks if the content of two items is the same.
         *
         * @param oldItem The previous DailyForecastElement.
         * @param newItem The new DailyForecastElement.
         * @return `true` if the content of both items is the same; otherwise, `false`.
         */
        override fun areContentsTheSame(
            oldItem: DailyForecastElement,
            newItem: DailyForecastElement
        ): Boolean {
            return oldItem == newItem
        }
    }
}
