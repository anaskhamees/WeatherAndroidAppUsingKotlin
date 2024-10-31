package com.example.weatherforecast.DataSrc.local.alert

import com.example.weatherforecast.Model.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlertDataSource {
    suspend fun setAlarm(timeInMillis: Long, alarmId: Int)
    suspend fun deleteAlarm(alarm: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}