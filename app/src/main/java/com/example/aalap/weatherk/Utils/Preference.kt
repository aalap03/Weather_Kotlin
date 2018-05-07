package com.example.aalap.weatherk.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class Preference {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    companion object {
        var SELECTED_LATITUDE = "Current_Latitude"
        var SELECTED_LONGITUDE = "Current_Longitude"
        var USER_LATITUDE = "User_Latitude"
        var USER_LONGITUDE = "User_Longitude"
    }

    constructor(context: Context) {
        sharedPreferences = context.getSharedPreferences("Weather", MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun setSelectedLocation(latitude: Double, longitude: Double) {
        editor.putString(SELECTED_LATITUDE, latitude.toString()).commit()
        editor.putString(SELECTED_LONGITUDE, longitude.toString()).commit()
    }

    fun setUserLocation(latitude: Double, longitude: Double) {
        editor.putString(USER_LATITUDE, latitude.toString()).commit()
        editor.putString(USER_LONGITUDE, longitude.toString()).commit()
    }

    fun getUserLatitude(): Double {
        return if (sharedPreferences.getString(USER_LATITUDE, "").isEmpty()) -1.0
        else sharedPreferences.getString(USER_LATITUDE, "").toDouble()
    }

    fun getUserLongitude(): Double {
        return if (sharedPreferences.getString(USER_LONGITUDE, "").isEmpty()) -1.0
        else sharedPreferences.getString(USER_LONGITUDE, "").toDouble()
    }

    fun getSelectedtLatitude(): Double {
        return if (sharedPreferences.getString(SELECTED_LATITUDE, "").isEmpty()) -1.0
        else sharedPreferences.getString(SELECTED_LATITUDE, "").toDouble()
    }

    fun getSelectedLongitude(): Double {
        return if (sharedPreferences.getString(SELECTED_LONGITUDE, "").isEmpty()) -1.0
        else sharedPreferences.getString(SELECTED_LONGITUDE, "").toDouble()
    }
}