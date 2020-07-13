package com.fredrikbogg.weatherapp.models

class HourlyWeather(var time: String, var temp: String, var iconURL: String) {
    init {
        temp = "$temp°"
    }
}