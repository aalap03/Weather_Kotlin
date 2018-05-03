package com.example.aalap.weatherk.Model.DailyKotlin

import com.google.gson.annotations.SerializedName

data class Daily(
        @SerializedName("data") var data: List<DailyData>


) {
    override fun toString(): String {
        return "Daily(data=$data)"
    }
}