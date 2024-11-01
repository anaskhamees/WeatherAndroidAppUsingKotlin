package com.example.weatherforecast.Features.GoogleMaps.viewModel

import androidx.lifecycle.ViewModel
import com.example.weatherforecast.Data.Repository.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {

    fun saveLocation(latitude: Float, longitude: Float) {
        repository.saveLocation(latitude, longitude)
    }

    fun getLocation(): Pair<Float, Float>? {
        return repository.getLocation()
    }

}