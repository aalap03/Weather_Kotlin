package com.example.aalap.weatherk.Utils

import com.example.aalap.weatherk.R

class Utils {

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
    }
}