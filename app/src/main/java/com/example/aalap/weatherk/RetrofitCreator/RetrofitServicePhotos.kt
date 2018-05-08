package com.example.aalap.weatherk.RetrofitCreator

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface RetrofitServicePhotos {

    //        val api2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+
//        "&key="+"AIzaSyC-H5AtUPSdpX7ZnCG7YsKeX4vpJQpGni0"

    @GET("json")
    fun getResult(@Query("latlng") latLngConcat:String,
                  @Query("key") key:String): Observable<Response<ResponseBody>>
}