package com.example.aalap.weatherk.RetrofitCreator

import com.example.aalap.weatherk.Model.Forecast.Forecast
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServiceWeather {

    @GET("{latitude}, {longitude}")
    fun getWeather(
            @Path("latitude") latitude: Double
            , @Path("longitude") longitude: Double) : Observable<Response<Forecast>>
}
