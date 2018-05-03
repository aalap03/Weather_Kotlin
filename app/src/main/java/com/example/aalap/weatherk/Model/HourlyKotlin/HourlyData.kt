package com.example.aalap.weatherk.Model.HourlyKotlin

import com.example.aalap.weatherk.Interfaces.ConvertFerenheite
import com.example.aalap.weatherk.Interfaces.Icon
import com.example.aalap.weatherk.Utils.Companion.getCelcious
import com.example.aalap.weatherk.Utils.Companion.getIcon
import com.google.gson.annotations.SerializedName

data class HourlyData(
        @SerializedName("time") var time: Int, //1525305600
        @SerializedName("summary") var summary: String, //Clear
        @SerializedName("icon") var icon: String, //clear-day
        @SerializedName("temperature") var temperature: Double, //61.43
        @SerializedName("apparentTemperature") var apparentTemperature: Double /*61.43*/) : ConvertFerenheite, Icon {

    override fun icon(): Int {
        return getIcon(icon)
    }

    override fun getTemperature(): Int {
        return getCelcious(temperature)
    }

    override fun getFeelsLike(): Int {
        return getCelcious(apparentTemperature)
    }


    override fun toString(): String {
        return "HourlyData(time=$time, summary='$summary', icon='$icon', temperature=$temperature, apparentTemperature=$apparentTemperature)"
    }

}

