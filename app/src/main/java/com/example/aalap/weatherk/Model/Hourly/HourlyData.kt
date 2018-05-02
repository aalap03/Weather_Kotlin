package com.example.aalap.weatherk.Model.Hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Aalap on 2018-03-31.
 */

class HourlyData {
    @SerializedName("time")
    @Expose
    var time: Int? = null
    @SerializedName("summary")
    @Expose
    var summary: String? = null
    @SerializedName("icon")
    @Expose
    var icon: String? = null
    @SerializedName("temperature")
    @Expose
    private var temperature: Double? = null
    @SerializedName("apparentTemperature")
    @Expose
    private var apparentTemperature: Double? = null

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
     * @param apparentTemperature
     * @param temperature
     */
    constructor(time: Int?, summary: String, icon: String, temperature: Double?, apparentTemperature: Double?) : super() {
        this.time = time
        this.summary = summary
        this.icon = icon
        this.temperature = temperature
        this.apparentTemperature = apparentTemperature
    }

    fun getTemperature(): Int {
        return ((temperature!! - 32) / 1.8).toInt()
    }

    fun setTemperature(temperature: Double?) {
        this.temperature = temperature
    }

    fun getApparentTemperature(): Int {
        return ((apparentTemperature!! - 32) / 1.8).toInt()
    }

    fun setApparentTemperature(apparentTemperature: Double?) {
        this.apparentTemperature = apparentTemperature
    }

    override fun toString(): String {
        return "HourlyData{" +
                "time=" + time +
                ", summary='" + summary + '\'' +
                ", icon='" + icon + '\'' +
                ", temperature=" + temperature +
                ", apparentTemperature=" + apparentTemperature +
                '}'
    }
}
