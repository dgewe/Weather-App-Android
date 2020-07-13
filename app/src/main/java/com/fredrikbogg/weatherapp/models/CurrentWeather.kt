package com.fredrikbogg.weatherapp.models

class CurrentWeather(
    var city: String,
    var description: String,
    var temp: String,
    var minTemp: String,
    var maxTemp: String,
    var iconURL: String
) {
    init {
        description = description.capitalize()
        temp = " $temp°"
        minTemp = "$minTemp°"
        maxTemp = "$maxTemp°"
    }
}