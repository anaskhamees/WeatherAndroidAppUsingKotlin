package com.example.weatherforecast.Features.Favourites.viewModel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.Model.WeatherEntity
import com.example.weatherforecast.databinding.ItemFavouriteBinding
import com.example.weatherforecast.Features.Settings.viewModel.SettingsViewModel
import com.example.weatherforecast.Utils.Constants.TEMPERATURE_FORMAT
import com.example.weatherforecast.Utils.Helpers.convertTempUnitFromCelsiusToAny
import com.example.weatherforecast.Utils.Helpers.getTempUnitSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritesAdapter(
    private val settingsViewModel: SettingsViewModel,
    private val lifecycleScope: CoroutineScope,
    private val onItemClicked: (String) -> Unit
) : ListAdapter<WeatherEntity, FavouritesAdapter.FavouritesViewHolder>(FavouritesDiffCallback()) {

    class FavouritesDiffCallback : DiffUtil.ItemCallback<WeatherEntity>() {
        override fun areItemsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
            return oldItem.cityName == newItem.cityName
        }

        override fun areContentsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesAdapter.FavouritesViewHolder {
        val binding =
            ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouritesAdapter.FavouritesViewHolder, position: Int) {
        val weatherEntity = getItem(position)
        holder.bind(weatherEntity)
    }

    inner class FavouritesViewHolder(private val binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("DefaultLocale")
        fun bind(weatherEntity: WeatherEntity) {
            val animation: Animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_in_animation)
            binding.root.startAnimation(animation)

            binding.root.setOnClickListener {
                onItemClicked(weatherEntity.cityName)
            }

            lifecycleScope.launch(Dispatchers.Main) {
                val unit = settingsViewModel.getTemperatureUnit()
                binding.tvTempunitLabel.text = weatherEntity.cityName

                // Ensure both arguments are passed to format string
                binding.tvTempunitValue.text = String.format(
                    TEMPERATURE_FORMAT,
                    convertTempUnitFromCelsiusToAny(weatherEntity.currentTemp, unit),
                    getTempUnitSymbol(unit)
                )
            }
        }
    }
}