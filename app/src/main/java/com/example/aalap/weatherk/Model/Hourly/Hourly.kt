package com.example.aalap.weatherk.Model.Hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by Aalap on 2018-03-31.
 */

class Hourly {
    @SerializedName("data")
    @Expose
    private var data: ArrayList<HourlyData>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param data
     */
    constructor(data: ArrayList<HourlyData>) : super() {
        this.data = data
    }

    fun getData(): List<HourlyData>? {
        return data
    }

    fun setData(data: ArrayList<HourlyData>) {
        this.data = data
    }

    override fun toString(): String {
        return "Hourly{" +
                "data=" + data +
                '}'
    }

}
