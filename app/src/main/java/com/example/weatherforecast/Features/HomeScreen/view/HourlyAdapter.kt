package com.example.weatherforecast.Features.HomeScreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.Model.HourlyListElement
import com.example.weatherforecast.databinding.ItemHourlyBinding
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModel
import com.example.weatherforecast.Utils.Constants.TEMPERATURE_FORMAT
import com.example.weatherforecast.Utils.Helpers.convertTempUnitFromCelsiusToAny
import com.example.weatherforecast.Utils.Helpers.formatTime
import com.example.weatherforecast.Utils.Helpers.getHourFromUnixTime
import com.example.weatherforecast.Utils.Helpers.getTempUnitSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Adapter for displaying hourly weather data in a RecyclerView.
 *
 * @param settingsViewModel ViewModel containing the user's temperature unit preference.
 * @param lifecycleScope Scope for launching coroutines, typically linked to the lifecycle of the activity or fragment.
 */
class HourlyAdapter(
    private val settingsViewModel: SettingsViewModel,
    private val lifecycleScope: CoroutineScope
) : ListAdapter<HourlyListElement, HourlyAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffCallback()) {

    /**
     * Creates and returns a ViewHolder for an hourly weather item.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return An instance of HourlyWeatherViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        // Inflate the layout for each item using ItemHourlyBinding
        val binding = ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    /**
     * Binds data to the ViewHolder and starts an animation.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the adapter.
     */
    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        // Load and start the animation for scaling in each item
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_in_animation)
        holder.itemView.startAnimation(animation)

        // Bind the data for the item at the specified position
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder class for hourly weather items.
     *
     * @param binding Data binding for the hourly weather item layout.
     */
    inner class HourlyWeatherViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds hourly weather data to the views.
         *
         * @param hourlyWeather The hourly weather data to bind.
         */
        fun bind(hourlyWeather: HourlyListElement) {
            lifecycleScope.launch(Dispatchers.Main) {
                // Retrieve the temperature unit setting from the settingsViewModel
                val unit = settingsViewModel.getTemperatureUnit()

                // Convert the temperature from Celsius to the selected unit
                val temp = convertTempUnitFromCelsiusToAny(hourlyWeather.main.temp, unit)

                // Format the temperature and set it to the temperature TextView
                binding.tvDegreeDayHour.text = String.format(TEMPERATURE_FORMAT, temp, getTempUnitSymbol(unit))

                // Set the formatted time of the hourly forecast
                binding.timeHour.text = formatTime(hourlyWeather.dt)

                // Determine the correct icon based on the hour of the day
                val hour = getHourFromUnixTime(hourlyWeather.dt)
                val icon = when (hour) {
                    6, 9, 12, 15, 18 -> R.drawable.ic_day_hour // Use day icon for daytime hours
                    else -> R.drawable.ic_night_hour           // Use night icon for other hours
                }

                // Set the determined icon to the ImageView
                binding.imvWeatherHour.setImageResource(icon)
            }
        }
    }

    /**
     * DiffUtil callback for calculating the difference between two hourly weather items.
     * Optimizes the performance of the RecyclerView by only updating items that have changed.
     */
    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyListElement>() {

        /**
         * Checks if two items represent the same hourly forecast.
         *
         * @param oldItem The old item.
         * @param newItem The new item.
         * @return True if the items are the same, false otherwise.
         */
        override fun areItemsTheSame(oldItem: HourlyListElement, newItem: HourlyListElement): Boolean {
            return oldItem.dt == newItem.dt // Compare based on unique timestamp (dt)
        }

        /**
         * Checks if the contents of two items are the same.
         *
         * @param oldItem The old item.
         * @param newItem The new item.
         * @return True if the contents are the same, false otherwise.
         */
        override fun areContentsTheSame(oldItem: HourlyListElement, newItem: HourlyListElement): Boolean {
            return oldItem == newItem // Compare all properties using data class equality
        }
    }
}
