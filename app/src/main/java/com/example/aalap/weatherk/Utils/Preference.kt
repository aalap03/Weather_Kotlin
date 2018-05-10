package com.example.aalap.weatherk.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.vicpin.krealmextensions.queryFirst

class Preference {

    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor

    companion object {
        var CITYID_SELECTED = "city_selected"
        var CITYID_USER = "city_user"
    }

    constructor(context: Context) {
        sharedPreferences = context.getSharedPreferences("Weather", MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun setSelectedCity(cityId: String) {
        editor.putString(CITYID_SELECTED, cityId).commit()
    }

    fun setUserCity(cityId: String) {
        editor.putString(CITYID_USER, cityId).commit()
    }

    fun getSelectedCity():City?{
        val cityId = sharedPreferences.getString(CITYID_SELECTED, "")
        return City().queryFirst { equalTo("id", cityId) }
    }

    fun getUserCity():City?{
        val cityId = sharedPreferences.getString(CITYID_USER, "")
        return City().queryFirst { equalTo("id", cityId) }
    }
}