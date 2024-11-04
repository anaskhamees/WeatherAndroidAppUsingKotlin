// Package declaration indicating the location of this class in the project structure.
package com.example.weatherforecast.Data.DataBase.local.alert

import android.annotation.SuppressLint // Importing annotation to suppress lint warnings.
import android.app.AlarmManager // Importing AlarmManager for scheduling alarms.
import android.app.PendingIntent // Importing PendingIntent to handle intent operations.
import android.content.Context // Importing Context to access application environment.
import android.content.Intent // Importing Intent for defining operations to be performed.
import com.example.weatherforecast.Data.DataBase.room.AlarmDao // Importing AlarmDao for database operations.
import com.example.weatherforecast.Data.Model.AlarmEntity // Importing AlarmEntity model for representing alarms.
import com.example.weatherforecast.Services.alertAlarm.AlarmReceiver // Importing AlarmReceiver for handling alarm broadcasts.
import com.example.weatherforecast.Services.alertAlarm.AlarmService // Importing AlarmService for managing alarm services.
import com.example.weatherforecast.Utils.Constants.ALARM_ID_TO_DISMISS // Importing constant for alarm dismissal.
import kotlinx.coroutines.flow.Flow // Importing Flow for handling asynchronous data streams.

class AlertDataSourceImpl(
    private val context: Context, // Application context for accessing system services.
    private val alertDao: AlarmDao, // DAO for performing database operations on alarms.
    private val alarmManager: AlarmManager // AlarmManager for scheduling and managing alarms.
) : AlertDataSource { // Implementing the AlertDataSource interface.

    // Suppresses lint warnings regarding exact alarm scheduling.
    @SuppressLint("ScheduleExactAlarm")
    override suspend fun setAlarm(timeInMillis: Long, alarmId: Int) {
        // Creates an intent for AlarmReceiver, passing the alarm ID to dismiss it later.
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID_TO_DISMISS, alarmId) // Attaching alarm ID as an extra to the intent.
        }

        // Creating a PendingIntent for the alarm that will be triggered at the specified time.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId, // Unique ID for the alarm.
            intent, // The intent to be triggered when the alarm goes off.
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // Flags for the PendingIntent.
        )

        // Setting the alarm to trigger at the specified time using the alarm manager.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        // Creating an AlarmEntity to store alarm details in the database.
        val alarmEntity = AlarmEntity(alarmId = alarmId, timeInMillis = timeInMillis)
        alertDao.insertAlarm(alarmEntity) // Inserting the alarm into the database.
    }

    // Function to delete an existing alarm from the system and the database.
    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        // Creating an intent for AlarmReceiver to identify which alarm to cancel.
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmId, // Unique ID for the alarm.
            intent, // The intent to be canceled.
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // Flags for the PendingIntent.
        )

        // Cancelling the alarm using the alarm manager.
        alarmManager.cancel(pendingIntent)

        // Stopping the AlarmService if it is currently running.
        val stopIntent = Intent(context, AlarmService::class.java)
        context.stopService(stopIntent)

        // Deleting the alarm from the database.
        alertDao.deleteAlarm(alarm)
    }

    // Function to retrieve all alarms as a Flow of a list of AlarmEntity objects.
    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alertDao.getAllAlarms() // Returning the flow of alarms from the database.
    }
}
