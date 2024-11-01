package com.example.weatherforecast.Features.HomeScreen.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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

class DailyAdapter(
    private val settingsViewModel: SettingsViewModel,
    private val lifecycleScope: CoroutineScope
) :
    ListAdapter<DailyForecastElement, DailyAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyAdapter.DailyWeatherViewHolder {
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyAdapter.DailyWeatherViewHolder, position: Int) {
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_bottom)
        holder.bind(getItem(position))
        holder.itemView.startAnimation(animation)
    }

    inner class DailyWeatherViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DefaultLocale")
        fun bind(dailyWeather: DailyForecastElement) {
            lifecycleScope.launch(Dispatchers.Main) {
                val unit = settingsViewModel.getTemperatureUnit()

                // Convert the timestamp to a LocalDate
                val date = Instant.ofEpochSecond(dailyWeather.dt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Check if the date is today
                val today = LocalDate.now()
                val dayString = when (date) {
                    today -> getString(itemView.context, R.string.today)
                    today.plusDays(1) -> getString(itemView.context, R.string.tomorrow)
                    else -> date.format(
                        DateTimeFormatter.ofPattern(
                            "EEEE",
                            Locale.getDefault()
                        )
                    ) // Day of the week
                }
                val maxTemp = convertTempUnitFromCelsiusToAny(dailyWeather.main.temp_max, unit)
                val minTemp = convertTempUnitFromCelsiusToAny(dailyWeather.main.temp_min, unit)
                binding.tvDayDays.text = dayString

                // Set weather details
                binding.tvWeatherCondition.text = dailyWeather.weather[0].description
                    .split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                binding.tvHighDegree.text = String.format("%.0f", maxTemp, getTempUnitSymbol(unit))
                binding.tvLowDegree.text =
                    String.format(TEMPERATURE_FORMAT, minTemp, getTempUnitSymbol(unit))

                val iconCode = dailyWeather.weather[0].icon
                binding.ivIconDays.setImageResource(getCustomIconForWeather(iconCode))
            }
        }
    }

    private fun getCustomIconForWeather(iconCode: String): Int {
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

    class DailyWeatherDiffCallback : DiffUtil.ItemCallback<DailyForecastElement>() {
        override fun areItemsTheSame(
            oldItem: DailyForecastElement,
            newItem: DailyForecastElement
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: DailyForecastElement,
            newItem: DailyForecastElement
        ): Boolean {
            return oldItem == newItem
        }
    }
}



