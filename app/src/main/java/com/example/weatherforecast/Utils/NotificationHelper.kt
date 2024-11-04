package com.example.weatherforecast.Utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.weatherforecast.R

/**
 * A utility object that provides helper methods for displaying and managing notifications in the app.
 */
object NotificationHelper {

    /**
     * Displays a notification with the specified title, content, and image.
     *
     * @param context The context used to access system services and resources.
     * @param title The title of the notification.
     * @param content The content text of the notification.
     * @param imageRes The resource ID of the image to be displayed in the notification.
     * @param id The unique identifier for the notification. Defaults to a random integer between 0 and 1000.
     */
    fun showNotification(
        context: Context,
        title: String,
        content: String,
        imageRes: Int,
        id: Int = (0..1000).random() // Generates a random ID if not provided
    ) {
        // Obtain the NotificationManager to manage notifications
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the Android version supports notification channels (API 26 and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a notification channel for Android O and above
            val channel = NotificationChannel(
                "WeatherAlarmChannel", // Unique channel ID
                "Weather Alarm Notification", // User-visible name for the channel
                NotificationManager.IMPORTANCE_HIGH // Importance level for notifications in this channel
            )
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification using NotificationCompat
        val notification = NotificationCompat.Builder(context, "WeatherAlarmChannel")
            .setSmallIcon(R.drawable.app_logo) // Icon shown in the status bar
            .setContentTitle(title) // Title displayed in the notification
            .setContentText(content) // Content text displayed in the notification
            .setLargeIcon(context.getDrawable(imageRes)?.toBitmap()) // Large icon for the notification
            .setAutoCancel(true) // Automatically dismiss the notification when clicked
            .build() // Build the notification instance

        // Notify the user with the created notification
        notificationManager.notify(id, notification) // Notify with the specified ID
    }

    /**
     * Dismisses a notification with the specified ID.
     *
     * @param context The context used to access system services.
     * @param id The unique identifier of the notification to dismiss.
     */
    fun dismissNotification(context: Context, id: Int) {
        // Obtain the NotificationManager to manage notifications
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Cancel the notification with the specified ID
        notificationManager.cancel(id)
    }
}
