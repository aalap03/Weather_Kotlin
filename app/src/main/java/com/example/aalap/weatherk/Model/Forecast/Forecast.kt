package com.example.aalap.weatherk.Model.Forecast
import com.example.aalap.weatherk.Model.Currently.Currently
import com.example.aalap.weatherk.Model.DailyKotlin.Daily
import com.example.aalap.weatherk.Model.HourlyKotlin.Hourly
import com.google.gson.annotations.SerializedName



data class Forecast(
        @SerializedName("timezone") var timezone: String, //America/Los_Angeles
        @SerializedName("currently") var currently: Currently,
        @SerializedName("hourly") var hourly: Hourly,
        @SerializedName("daily") var daily: Daily
){
    override fun toString(): String {
        return "Forecast(timezone='$timezone', currently=$currently, hourly=$hourly, daily=$daily)"
    }
}

