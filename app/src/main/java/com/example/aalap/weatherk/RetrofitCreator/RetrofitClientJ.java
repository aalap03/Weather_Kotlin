package com.example.aalap.weatherk.RetrofitCreator;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientJ {

    public static Retrofit getRetrofit(){


        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.darksky.net/forecast/c8d13033595ad94a21a2465f54552730/")
                .build();
    }
}
