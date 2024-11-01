package com.example.weatherforecast.Features.Alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.Data.DataBase.local.alert.AlertDataSource

class AlertViewModelFactory(private val alertDataSource: AlertDataSource) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertViewModel(alertDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}