package com.fredrikbogg.weatherapp.models

class DailyWeather(var day: String, var iconURL: String, var maxTemp: String, var minTemp: String){
    init {
        minTemp = "$minTemp°"
        maxTemp = "$maxTemp°"
    }
}