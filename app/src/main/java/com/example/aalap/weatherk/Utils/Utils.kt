package com.example.aalap.weatherk.Utils

import com.example.aalap.weatherk.R

class Utils {

    val TAG = "Utils:"

    companion object {
        fun getCelcious(ferenheite: Double): Int {
            return ((ferenheite - 32) * 5 / 9).toInt()
        }

        //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
        // (Developers should ensure that a sensible default is defined, as additional values, such as hail, thunderstorm, or tornado

        fun getIcon(icon: String): Int =
                when (icon) {
                    "clear-day" -> R.drawable.wclear_day

                    "clear-night" -> R.drawable.wclear_night

                    "cloudy" -> R.drawable.wcloudy

                    "partly-cloudy-day" -> R.drawable.wcloudy

                    "cloudy-night" -> R.drawable.wcloudy_night

                    "partly-cloudy-night" -> R.drawable.wcloudy_night

                    "fog" -> R.drawable.wfog

                    "wind" -> R.drawable.wind

                    "sleet" -> R.drawable.wind

                    "partly-cloudy" -> R.drawable.wpartly_cloudy

                    "rain" -> R.drawable.wrain

                    "snow" -> R.drawable.wsnow

                    else -> R.drawable.wclear_day
                }

        fun getBackground(icon: String): Int =
                when (icon) {
                    "clear-day" -> R.drawable.back_clearday

                    "clear-night" -> R.drawable.back_clearnight

                    "cloudy" -> R.drawable.back_cloudy

                    "partly-cloudy-day" -> R.drawable.back_cloudy

                    "cloudy-night" -> R.drawable.back_cloudynight

                    "partly-cloudy-night" -> R.drawable.back_cloudynight

                    "fog" -> R.drawable.back_foggy

                    "wind" -> R.drawable.back_wind

                    "sleet" -> R.drawable.back_wind

                    "partly-cloudy" -> R.drawable.back_cloudy

                    "rain" -> R.drawable.back_rain

                    "snow" -> R.drawable.back_snow

                    else -> R.drawable.wclear_day
                }
    }

}