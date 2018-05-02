package com.example.aalap.weatherk.RetrofitCreator

class TestJava {

    fun testJava() {

        val retrofitService = RetrofitClientJ.getRetrofit().create(RetrofitService::class.java)
    }
}
