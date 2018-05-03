package com.example.aalap.weatherk.RetrofitCreator

class TestJava {

    fun testJava() {
        val retrofitService = RetrofitClientJ.retrofit.create(RetrofitService::class.java)
    }
}
