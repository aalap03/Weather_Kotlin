package com.example.aalap.weatherk.RetrofitCreator

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {

    @GET("{latitude, longitude}")
    fun getWeather(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double) : Observable<Response<ResponseBody>>
}
