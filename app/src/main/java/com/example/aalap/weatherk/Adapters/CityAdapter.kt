package com.example.aalap.weatherk.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.aalap.weatherk.R
import com.example.aalap.weatherk.Utils.City
import com.vicpin.krealmextensions.delete

class CityAdapter(var context: Context, var cities:MutableList<City>): RecyclerView.Adapter<CityAdapter.CityHolder>() {

    var TAG = "CityAdapter:"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        return CityHolder(LayoutInflater.from(context).inflate(R.layout.city_item, parent, false))
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CityHolder, position: Int) {
        val city = cities.get(position)
        holder.name.text = city.name
        holder.remove.setOnClickListener({ view: View? ->
            cities.removeAt(position)
            City().delete { equalTo("id", city.id) }
            notifyItemRemoved(position)
        })
    }

    class CityHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var name:TextView = itemView!!.findViewById(R.id.city_name)
        var remove: ImageView = itemView!!.findViewById(R.id.city_remove)
    }
}