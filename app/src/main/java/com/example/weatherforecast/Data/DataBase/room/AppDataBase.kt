package com.example.weatherforecast.Data.DataBase.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.Data.Model.AlarmEntity
import com.example.weatherforecast.Data.Model.WeatherEntity

@Database(entities = [WeatherEntity::class, AlarmEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Weather_DB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}