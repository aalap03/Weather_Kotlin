package com.example.aalap.weatherk.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.aalap.weatherk.Model.Daily.DailyData
import com.example.aalap.weatherk.R

/**
 * Created by Aalap on 2018-03-30.
 */

class AdapterHourly(private var context: Context, private var items: ArrayList<DailyData>) : RecyclerView.Adapter<AdapterHourly.VHHourly>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHHourly {
        return VHHourly(LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false))
    }

    override fun onBindViewHolder(holder: VHHourly, position: Int) {

        var daily: DailyData = items.get(position)
        holder!!.temp.setText(daily.getTemperatureMax())
    }


    override fun getItemCount(): Int {
        return items.size
    }

//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VHHourly {
//        return VHHourly(LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false))
//    }

    class VHHourly(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView!!.findViewById(R.id.iconImageView)
        var temp: TextView = itemView!!.findViewById(R.id.temperatureLabel)
        var time: TextView = itemView!!.findViewById(R.id.timeLabel)
    }

}