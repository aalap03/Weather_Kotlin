package com.example.aalap.weatherk.Utils

import android.app.Application
import android.content.Context

class App: Application() {

    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

    }

}