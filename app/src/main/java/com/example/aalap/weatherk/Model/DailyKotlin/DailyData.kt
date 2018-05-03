package com.example.aalap.weatherk.Model.DailyKotlin

import com.example.aalap.weatherk.Interfaces.Icon
import com.example.aalap.weatherk.Utils.Companion.getCelcious
import com.example.aalap.weatherk.Utils.Companion.getIcon
import com.google.gson.annotations.SerializedName

data class DailyData(
        @SerializedName("time") var time: Int, //1509944400
        @SerializedName("summary") var summary: String, //Rain starting in the afternoon, continuing until evening.
        @SerializedName("icon") var icon: String, //rain
        @SerializedName("temperatureMin") var temperatureMin: Double, //52.08
        @SerializedName("temperatureMax") var temperatureMax: Double //66.35
) : Icon {
    override fun icon(): Int {
        return getIcon(icon)
    }

    fun getTemperatureMax(): Int {
        return getCelcious(temperatureMax)
    }

    fun getTemperatureMin(): Int {
        return getCelcious(temperatureMin)
    }

    override fun toString(): String {
        return "DailyData(time=$time, summary='$summary', icon='$icon', temperatureMin=$temperatureMin, temperatureMax=$temperatureMax)"
    }


}