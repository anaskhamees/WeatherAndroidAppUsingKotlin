package com.example.weatherforecast.Features.Alert.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.Data.DataBase.local.alert.AlertDataSource
import com.example.weatherforecast.Data.Model.AlarmEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel(private val alertDataSource: AlertDataSource) : ViewModel() {

    private val _alarms = MutableLiveData<List<AlarmEntity>>()
    val alarms: LiveData<List<AlarmEntity>> get() = _alarms


    fun setAlarm(timeInMillis: Long) {
        val alarmId = System.currentTimeMillis().toInt()
        viewModelScope.launch {
            alertDataSource.setAlarm(timeInMillis, alarmId)
        }
    }

    fun loadAlarms() {
        viewModelScope.launch {
            alertDataSource.getAllAlarms().collectLatest { alarmList ->
                _alarms.postValue(alarmList)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alertDataSource.deleteAlarm(alarm)
        }
    }

}