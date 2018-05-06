package com.example.aalap.weatherk.Utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.aalap.weatherk.RetrofitCreator.RetrofitClient
import com.example.aalap.weatherk.RetrofitCreator.RetrofitService
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

        fun retrofit():RetrofitService{
            return  RetrofitClient().getRetrofit().create(RetrofitService::class.java)
        }
    }



}