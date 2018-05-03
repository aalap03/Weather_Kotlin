package com.example.aalap.weatherk.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.aalap.weatherk.Model.DailyKotlin.DailyData
import com.example.aalap.weatherk.Model.HourlyKotlin.HourlyData
import com.example.aalap.weatherk.R

/**
 * Created by Aalap on 2018-03-30.
 */

class AdapterDaily(private var context: Context, private var items: List<DailyData>) : RecyclerView.Adapter<AdapterDaily.VHHourly>() {

    var TAG = "AdapterHourly:"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHHourly {
        return VHHourly(LayoutInflater.from(context).inflate(R.layout.item_daily, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VHHourly, position: Int) {
        val item = items.get(position)
        Log.d(TAG, ""+position+item.toString())

        holder.image.setImageResource(item.icon())
        holder.tempMax.text = ""+item.getTemperatureMax()
        holder.tempMin.text = ""+item.getTemperatureMin()
        holder.desc.text = ""+item.summary
        holder.day.text = ""+item.getDayOfTheWeek()
    }


    override fun getItemCount(): Int {
        return items.size
    }

    class VHHourly(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView!!.findViewById(R.id.weather_icon)
        var day: TextView = itemView!!.findViewById(R.id.day)
        var tempMin: TextView = itemView!!.findViewById(R.id.min_temp)
        var tempMax: TextView = itemView!!.findViewById(R.id.max_temp)
        var desc: TextView = itemView!!.findViewById(R.id.description)
    }
}