package com.example.weatherforecast.Data.DataBase.local.alert

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.weatherforecast.Data.DataBase.room.AlarmDao
import com.example.weatherforecast.Data.Model.AlarmEntity
import com.example.weatherforecast.Services.alertAlarm.AlarmReceiver
import com.example.weatherforecast.Services.alertAlarm.AlarmService
import com.example.weatherforecast.Utils.Constants.ALARM_ID_TO_DISMISS
import kotlinx.coroutines.flow.Flow

class AlertDataSourceImpl(
    private val context: Context,
    private val alertDao: AlarmDao,
    private val alarmManager: AlarmManager
) : AlertDataSource {
    @SuppressLint("ScheduleExactAlarm")
    override suspend fun setAlarm(timeInMillis: Long, alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID_TO_DISMISS, alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,  // Use alarmId here
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        val alarmEntity = AlarmEntity(alarmId = alarmId, timeInMillis = timeInMillis)
        alertDao.insertAlarm(alarmEntity)
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmId,  // Use alarmId here
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)

        // Stop the AlarmService (if running)
        val stopIntent = Intent(context, AlarmService::class.java)
        context.stopService(stopIntent)

        // Delete alarm from database
        alertDao.deleteAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alertDao.getAllAlarms()

    }
}