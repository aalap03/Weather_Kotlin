package com.example.aalap.weatherk.Model.Daily

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Aalap on 2017-10-16.
 */

class DailyData {

    @SerializedName("time")
    @Expose
    var time: Int? = null
    @SerializedName("summary")
    @Expose
    var summary: String? = null

    @SerializedName("timezone")
    @Expose
    var timezone: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null
    @SerializedName("temperatureMin")
    @Expose
    private var temperatureMin: Double? = null
    @SerializedName("temperatureMax")
    @Expose
    private var temperatureMax: Double? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param summary
     * @param icon
     * @param time
     * @param temperatureMin
     * @param temperatureMax
     */
    constructor(time: Int?, summary: String, icon: String, temperatureMin: Double?, temperatureMax: Double?) : super() {
        this.time = time
        this.summary = summary
        this.icon = icon
        this.temperatureMin = temperatureMin
        this.temperatureMax = temperatureMax
    }

    fun getTemperatureMin(): Int {
        return ((temperatureMin!! - 32) / 1.8).toInt()
    }

    fun setTemperatureMin(temperatureMin: Double?) {
        this.temperatureMin = temperatureMin
    }

    fun getTemperatureMax(): Int {
        return ((temperatureMax!! - 32) / 1.8).toInt()
    }

    fun setTemperatureMax(temperatureMax: Double?) {
        this.temperatureMax = temperatureMax
    }

    override fun toString(): String {
        return "DailyData{" +
                "time=" + time +
                ", summary='" + summary + '\'' +
                ", icon='" + icon + '\'' +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                '}'
    }

    companion object {
        private val TAG = "DailyData"
    }
}
