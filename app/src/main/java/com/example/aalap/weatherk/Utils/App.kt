package com.example.aalap.weatherk.Utils

import android.app.Application
import android.content.Context
import com.example.aalap.weatherk.RetrofitCreator.RetrofitClientPhotos
import com.example.aalap.weatherk.RetrofitCreator.RetrofitClientWeather
import com.example.aalap.weatherk.RetrofitCreator.RetrofitServicePhotos
import com.example.aalap.weatherk.RetrofitCreator.RetrofitServiceWeather
import io.realm.Realm
import io.realm.RealmConfiguration

class App: Application() {

    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val realmConfiguration = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)

    }

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun pref():Preference{
            return Preference(applicationContext())
        }

        fun retrofit():RetrofitServiceWeather{
            return  RetrofitClientWeather().getRetrofit().create(RetrofitServiceWeather::class.java)
        }

        fun retrofitPhotos():RetrofitServicePhotos{
            return  RetrofitClientPhotos().retrofit().create(RetrofitServicePhotos::class.java)
        }
    }



}