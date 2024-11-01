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

class HourlyAdapter(
    private val settingsViewModel: SettingsViewModel,
    private val lifecycleScope: CoroutineScope
) : ListAdapter<HourlyListElement, HourlyAdapter.HourlyWeatherViewHolder>
    (HourlyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding = ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_in_animation)
        holder.itemView.startAnimation(animation)
        holder.bind(getItem(position))
    }

    inner class HourlyWeatherViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hourlyWeather: HourlyListElement) {
            lifecycleScope.launch(Dispatchers.Main) {
                val unit = settingsViewModel.getTemperatureUnit()
                val temp = convertTempUnitFromCelsiusToAny(hourlyWeather.main.temp, unit)
                binding.tvDegreeDayHour.text =
                    String.format(TEMPERATURE_FORMAT, temp, getTempUnitSymbol(unit))

                //time and weather icon
                binding.timeHour.text = formatTime(hourlyWeather.dt)
                val hour = getHourFromUnixTime(hourlyWeather.dt)
                val icon = when (hour) {
                    6, 9, 12, 15, 18 -> R.drawable.ic_day_hour
                    else -> R.drawable.ic_night_hour
                }
                binding.imvWeatherHour.setImageResource(icon)
            }
        }

    }

    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyListElement>() {
        override fun areItemsTheSame(
            oldItem: HourlyListElement,
            newItem: HourlyListElement
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: HourlyListElement,
            newItem: HourlyListElement
        ): Boolean {
            return oldItem == newItem
        }
    }
}