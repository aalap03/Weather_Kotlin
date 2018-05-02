package com.example.aalap.weatherk.RetrofitCreator;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitService {

    @GET("{latitude}, {longitude}")
    Observable<Response<ResponseBody>> getWeather(@Path("latitude") double latitude, @Path("longitude") double longitude);
}
