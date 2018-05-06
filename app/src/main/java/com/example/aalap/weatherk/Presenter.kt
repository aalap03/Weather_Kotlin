package com.example.aalap.weatherk

import android.util.Log
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.RetrofitCreator.RetrofitClient
import com.example.aalap.weatherk.RetrofitCreator.RetrofitService
import com.example.aalap.weatherk.View.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Created by Aalap on 2018-03-31.
 */

class Presenter(var view: MainView) {

    val TAG = "Presenter"

    fun initiateWeatherRequest(latitude: Double, longitude: Double) {

        Log.d(TAG, "Requesting...")
        Log.d(TAG, latitude.toString())
        Log.d(TAG, longitude.toString())

        showProgress(true)

        val retrofitService = RetrofitClient().getRetrofit().create(RetrofitService::class.java)

        retrofitService.getWeather(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: Response<Forecast>? ->
                    if (response!!.isSuccessful) {
                        val forecast = response.body()
                        val timeZone = forecast!!.timezone

                        for (dailyData in forecast.daily.data){
                            dailyData.timeZone = timeZone
                        }
                        for (hourlyData in forecast.hourly.data){
                            hourlyData.timeZone = timeZone
                        }

                        view.showForecast(forecast)

                    } else {
                        throw Exception(response.errorBody()!!.string())
                    }
                }, { throwable -> view.showError(throwable.localizedMessage) })
    }

    fun showErrorMsg(localizedMessage: String?) {
        showProgress(false)
        view.showError(localizedMessage!!)
    }

    fun showProgress(isVisible: Boolean) {
        view.showProgress(isVisible)
    }


}
