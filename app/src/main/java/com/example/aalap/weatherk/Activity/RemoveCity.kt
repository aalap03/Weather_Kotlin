package com.example.aalap.weatherk.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.aalap.weatherk.Adapters.CityAdapter
import com.example.aalap.weatherk.R
import com.example.aalap.weatherk.Utils.City
import com.vicpin.krealmextensions.queryAll

class RemoveCity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_city)
        recyclerView = findViewById(R.id.city_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var list:MutableList<City> = mutableListOf()
        list.addAll(City().queryAll())
        recyclerView.adapter = CityAdapter(this, list)
    }
}
