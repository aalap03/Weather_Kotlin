package com.example.aalap.weatherk

import android.text.style.ForegroundColorSpan
import android.util.Log
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.RetrofitCreator.RetrofitClient
import com.example.aalap.weatherk.RetrofitCreator.RetrofitService
import com.example.aalap.weatherk.View.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by Aalap on 2018-03-31.
 */

class Presenter(var view: MainView) {



    val TAG="Presenter"

    fun initiateWeatherRequest() {
        var retrofitService = RetrofitClient().getRetrofit().create(RetrofitService::class.java)
        retrofitService.getWeather(37.8267, -122.4233)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: Response<Forecast>? ->
                    if(response!!.isSuccessful){
                        Log.d(TAG, "Result:"+ response.body())
                    }else{
                        Log.d(TAG, "Result:err "+ response.errorBody()!!.string())
                    }
                })
    }


}
