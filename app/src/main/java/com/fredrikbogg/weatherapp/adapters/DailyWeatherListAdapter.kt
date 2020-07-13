package com.fredrikbogg.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fredrikbogg.weatherapp.R
import com.fredrikbogg.weatherapp.models.DailyWeather
import com.squareup.picasso.Picasso

class DailyWeatherListAdapter(private val dataset: ArrayList<DailyWeather>) :
    RecyclerView.Adapter<DailyWeatherListAdapter.DailyViewHolder>() {

    class DailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var dailyDayTextView: TextView = view.findViewById(R.id.dailyDayTextView)
        var iconImageView: ImageView = view.findViewById(R.id.dailyTemperatureImageView)
        var dailyMaxTextView: TextView = view.findViewById(R.id.dailyMaxTempTextView)
        var dailyMinTextView: TextView = view.findViewById(R.id.dailyMinTempTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_daily_weather, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.dailyDayTextView.text = dataset[position].day
        holder.dailyMaxTextView.text = dataset[position].maxTemp
        holder.dailyMinTextView.text = dataset[position].minTemp
        Picasso.get().load(dataset[position].iconURL).into(holder.iconImageView)
    }

    override fun getItemCount() = dataset.size
}