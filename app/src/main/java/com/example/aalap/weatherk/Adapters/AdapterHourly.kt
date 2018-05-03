package com.example.aalap.weatherk.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.aalap.weatherk.Model.HourlyKotlin.HourlyData
import com.example.aalap.weatherk.R

/**
 * Created by Aalap on 2018-03-30.
 */

class AdapterHourly(private var context: Context, private var items: List<HourlyData>) : RecyclerView.Adapter<AdapterHourly.VHHourly>() {

    var TAG = "AdapterHourly:"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHHourly {
        return VHHourly(LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false))
    }

    override fun onBindViewHolder(holder: VHHourly, position: Int) {

        var hourly = items.get(position)
        Log.d(TAG, ""+position+hourly.toString())

        holder.image.setImageResource(hourly.icon())
        holder.time.text = hourly.getTimeAsHour()
        holder.temp.text = ""+hourly.getTemperature()
    }


    override fun getItemCount(): Int {
        return items.size
    }

    class VHHourly(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView!!.findViewById(R.id.iconImageView)
        var temp: TextView = itemView!!.findViewById(R.id.temperatureLabel)
        var time: TextView = itemView!!.findViewById(R.id.timeLabel)
    }
}