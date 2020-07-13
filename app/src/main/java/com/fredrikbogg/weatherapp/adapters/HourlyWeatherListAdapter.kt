package com.fredrikbogg.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fredrikbogg.weatherapp.R
import com.fredrikbogg.weatherapp.models.HourlyWeather
import com.squareup.picasso.Picasso

class HourlyWeatherListAdapter(private val dataset: ArrayList<HourlyWeather>) :
    RecyclerView.Adapter<HourlyWeatherListAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var temperatureTextView: TextView = view.findViewById(R.id.hourlyTemperatureTextView)
        var timeTextView: TextView = view.findViewById(R.id.hourlyTimeTextView)
        var iconImageView: ImageView = view.findViewById(R.id.hourlyTemperatureImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.temperatureTextView.text = dataset[position].temp
        holder.timeTextView.text = dataset[position].time
        Picasso.get().load(dataset[position].iconURL).into(holder.iconImageView)
    }

    override fun getItemCount() = dataset.size
}