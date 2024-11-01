package com.example.weatherforecast.Data.DataBase.local.alert

import com.example.weatherforecast.Data.Model.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlertDataSource {
    suspend fun setAlarm(timeInMillis: Long, alarmId: Int)
    suspend fun deleteAlarm(alarm: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}