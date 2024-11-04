package com.example.weatherforecast.Utils

import android.content.Context
import android.view.View
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.Model.Weather

object HomeScreenHelper {

    /**
     * Retrieves the appropriate Lottie animation resource ID based on the current weather description.
     * @param context The application context used to access string resources.
     * @param weather The Weather object containing weather data.
     * @return The resource ID of the Lottie animation corresponding to the weather description.
     */
    fun checkWeatherDescriptionForAnimation(context: Context, weather: Weather): Int {
        val lottieAnimation = when (weather.weather[0].description.lowercase()) {
            context.getString(R.string.clear_sky) -> R.raw.clear_sky_anim // Animation for clear sky
            context.getString(R.string.few_clouds) -> R.raw.few_clouds // Animation for few clouds
            context.getString(R.string.scattered_clouds) -> R.raw.scattered_clouds_anim // Animation for scattered clouds
            context.getString(R.string.broken_clouds) -> R.raw.broken_cloud_anim // Animation for broken clouds
            context.getString(R.string.overcast_clouds) -> R.raw.overcast_clouds_anim // Animation for overcast clouds
            "light intensity shower rain" -> R.raw.rain_anim // Animation for light rain
            context.getString(R.string.light_rain) -> R.raw.rain_anim // Animation for light rain
            context.getString(R.string.moderate_rain) -> R.raw.rain_anim // Animation for moderate rain
            context.getString(R.string.light_snow) -> R.raw.snow_anim // Animation for light snow
            context.getString(R.string.snow) -> R.raw.snow_anim // Animation for snow
            "thunderstorm" -> R.raw.thunderstorm // Animation for thunderstorm
            "mist" -> R.raw.mist // Animation for mist
            else -> R.raw.clear_sky_anim // Default animation for unrecognized descriptions
        }
        return lottieAnimation // Return the animation resource ID
    }

    /**
     * Animates the view to slide in from the bottom and scale to full size.
     * @param view The view to be animated.
     */
    fun slideInAndScaleView(view: View) {
        if (view.visibility != View.VISIBLE) { // Check if the view is not already visible
            view.apply {
                scaleX = 0f // Start scale at 0
                scaleY = 0f // Start scale at 0
                translationY = 100f // Start position 100 pixels below
                visibility = View.VISIBLE // Make the view visible

                // Animate scaling and sliding in
                animate()
                    .scaleX(1f) // Scale to full width
                    .scaleY(1f) // Scale to full height
                    .translationY(0f) // Move to original position
                    .setDuration(1000) // Duration of the animation
                    .setInterpolator(android.view.animation.DecelerateInterpolator()) // Smooth deceleration
                    .start() // Start the animation
            }
        }
    }

    /**
     * Performs a dynamic text animation on the view, including scaling, rotation, and fading.
     * @param view The view to be animated.
     */
    fun dynamicTextAnimation(view: View) {
        if (view.visibility != View.VISIBLE) { // Check if the view is not already visible
            view.apply {
                alpha = 0f // Start fully transparent
                scaleX = 0.5f // Start smaller
                scaleY = 0.5f // Start smaller
                rotation = -30f // Start with a slight rotation

                visibility = View.VISIBLE // Make the view visible

                // Animate with scaling, rotation, and fading
                animate()
                    .alpha(1f) // Fade in
                    .scaleX(1.2f) // Scale up slightly
                    .scaleY(1.2f)
                    .rotation(0f) // Reset rotation
                    .setDuration(1200) // Duration of the animation
                    .setInterpolator(android.view.animation.BounceInterpolator()) // Adds a fun bounce effect at the end
                    .withEndAction { // Return to original scale after bounce effect
                        animate()
                            .scaleX(1f) // Return to normal scale
                            .scaleY(1f)
                            .setDuration(300) // Duration of return animation
                            .start() // Start the return animation
                    }
                    .start() // Start the main animation
            }
        }
    }

    /**
     * Animates the view to slide in from the left side.
     * @param view The view to be animated.
     */
    fun slideInFromLeft(view: View) {
        if (view.visibility != View.VISIBLE) { // Check if the view is not already visible
            view.apply {
                alpha = 0f // Start fully transparent
                translationX = -200f // Start position 200 pixels to the left
                visibility = View.VISIBLE // Make the view visible

                // Animate the view with fading and sliding
                animate()
                    .alpha(1f) // Fade in
                    .translationX(0f) // Move to original position
                    .setDuration(1000) // Animation duration
                    .setInterpolator(android.view.animation.DecelerateInterpolator()) // Smooth deceleration
                    .start() // Start the animation
            }
        }
    }
}
