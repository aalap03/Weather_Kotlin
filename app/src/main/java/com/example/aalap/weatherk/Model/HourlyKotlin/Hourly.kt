package com.example.aalap.weatherk.Model.HourlyKotlin

import com.google.gson.annotations.SerializedName

data class Hourly(

    @SerializedName("data") var data: List<HourlyData>


) {
    override fun toString(): String {
        return "Hourly(data=$data)"
    }
}