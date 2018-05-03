package com.example.aalap.weatherk.Model.Currently

import com.example.aalap.weatherk.Utils.Utils.Companion.getCelcious
import com.google.gson.annotations.SerializedName


data class Currently(
        @SerializedName("time") var time: Int, //1525306111
        @SerializedName("summary") var summary: String, //Clear
        @SerializedName("icon") var icon: String, //clear-day
        @SerializedName("precipProbability") var precipProbability: Int, //0
        @SerializedName("temperature") var temperature: Double, //61.21
        @SerializedName("apparentTemperature") var apparentTemperature: Double, //61.21
        @SerializedName("humidity") var humidity: Double //0.66
){

    fun getCurrentTemperature(): Int{
        return getCelcious(temperature)
    }

    fun getFeelsLike(): Int {
        return getCelcious(apparentTemperature)
    }

    fun getHumidity():Int{
        return (humidity * 100).toInt()
    }

    override fun toString(): String {
        return "Currently(time=$time, summary='$summary', icon='$icon', precipProbability=$precipProbability, temperature=$temperature, apparentTemperature=$apparentTemperature, humidity=$humidity)"
    }


}


