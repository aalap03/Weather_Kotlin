package com.example.aalap.weatherk

import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.Model.WeatherModel
import com.example.aalap.weatherk.View.MainView

class Presenter(var view: MainView) {

    val TAG = "Presenter"

    var model:WeatherModel = WeatherModel(this)

    fun initiateWeatherRequest(latitude: Double, longitude: Double) {
        model.getForecast(latitude, longitude)
        model.getPlaceName(latitude, longitude)
    }

    fun initiatePlaceAPICall(latitude:Double, longitude:Double) {
        model.getPlaceInfo(latitude, longitude)
    }

    fun showErrorMsg(localizedMessage: String?) {
        showProgress(false)
        view.showError(localizedMessage!!)
    }

    fun showProgress(isVisible: Boolean) {
        view.showProgress(isVisible)
    }

    fun showForecast(forecast: Forecast) {
        view.showForecast(forecast)
    }

    fun getPlaceId(placeId: String?) {
        view.showPlaceInfo(placeId)
    }

    fun noPlaceId() {
        view.noPlaceId()
    }

    fun ShowPlaceName(locality: String?) {
        view.showPlaceName(locality)
    }

    fun requestPlaceName(latitude: Double, longitude: Double) {
        model.getPlaceName(latitude, longitude)
    }
}
