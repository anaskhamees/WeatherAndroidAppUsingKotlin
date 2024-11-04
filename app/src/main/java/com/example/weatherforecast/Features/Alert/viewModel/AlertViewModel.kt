// Package declaration indicating the location of this class in the project structure.
package com.example.weatherforecast.Features.Alert.viewModel

import androidx.lifecycle.LiveData // Importing LiveData to hold observable data.
import androidx.lifecycle.MutableLiveData // Importing MutableLiveData for mutable observable data.
import androidx.lifecycle.ViewModel // Importing ViewModel for managing UI-related data.
import androidx.lifecycle.viewModelScope // Importing viewModelScope for launching coroutines in the ViewModel.
import com.example.weatherforecast.Data.DataBase.local.alert.AlertDataSource // Importing the data source for alarms.
import com.example.weatherforecast.Data.Model.AlarmEntity // Importing the AlarmEntity model.
import kotlinx.coroutines.flow.collectLatest // Importing collectLatest to collect the latest emissions from a flow.
import kotlinx.coroutines.launch // Importing launch to start a coroutine.

class AlertViewModel(private val alertDataSource: AlertDataSource) : ViewModel() {

    // Private MutableLiveData to hold the list of alarms.
    private val _alarms = MutableLiveData<List<AlarmEntity>>()

    // Public LiveData to expose the alarms list to observers.
    val alarms: LiveData<List<AlarmEntity>> get() = _alarms

    // Function to set a new alarm.
    fun setAlarm(timeInMillis: Long) {
        // Generating a unique alarm ID based on the current time.
        val alarmId = System.currentTimeMillis().toInt()
        // Launching a coroutine to set the alarm in the data source.
        viewModelScope.launch {
            alertDataSource.setAlarm(timeInMillis, alarmId)
        }
    }

    // Function to load all alarms from the data source.
    fun loadAlarms() {
        // Launching a coroutine to collect the alarms from the data source.
        viewModelScope.launch {
            alertDataSource.getAllAlarms().collectLatest { alarmList ->
                // Posting the latest list of alarms to the MutableLiveData.
                _alarms.postValue(alarmList)
            }
        }
    }

    // Function to delete a specific alarm.
    fun deleteAlarm(alarm: AlarmEntity) {
        // Launching a coroutine to delete the alarm from the data source.
        viewModelScope.launch {
            alertDataSource.deleteAlarm(alarm)
        }
    }
}
