package com.example.aalap.weatherk.View

import com.example.aalap.weatherk.Model.Forecast.Forecast

/**
 * Created by Aalap on 2018-03-31.
 */

interface MainView{

    fun setRecyclerViews()
    fun setDrawerItems()
    fun showProgress(visible: Boolean)
    fun showError(errorMsg: String)
    fun showForecast(forecast: Forecast?)
    fun showPlaceInfo(placeId: String?)
    fun noPlaceId()
    fun showPlaceName(locality: String?)
}
