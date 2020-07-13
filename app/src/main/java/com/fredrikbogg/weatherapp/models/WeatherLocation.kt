package com.fredrikbogg.weatherapp.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

class WeatherLocation {
    @SerializedName("timezone_offset")
    lateinit var timezoneOffset: String

    @SerializedName("current")
    lateinit var current: LinkedTreeMap<Any, Any>

    @SerializedName("hourly")
    lateinit var hourly: ArrayList<LinkedTreeMap<Any, Any>>

    @SerializedName("daily")
    lateinit var daily: ArrayList<LinkedTreeMap<Any, Any>>

    fun getCurrentTemperature(): Double {
        return current["temp"] as Double
    }

    fun getCurrentDescription(): String {
        return ((current["weather"] as ArrayList<*>)[0] as LinkedTreeMap<*, *>)["description"].toString()
    }

    fun getCurrentIcon(): String {
        return ((current["weather"] as ArrayList<*>)[0] as LinkedTreeMap<*, *>)["icon"].toString()
    }

    fun getHourlyTemperature(hourIndex: Int): Double {
        return hourly[hourIndex]["temp"] as Double
    }

    fun getHourlyIcon(hourIndex: Int): String {
        return ((hourly[hourIndex]["weather"] as ArrayList<*>)[0] as LinkedTreeMap<*, *>)["icon"].toString()
    }

    fun getHourlyDataTime(hourIndex: Int): Double {
        return hourly[hourIndex]["dt"] as Double
    }

    fun getDailyMinTemperature(dayIndex: Int): Double {
        return (daily[dayIndex]["temp"] as LinkedTreeMap<*, *>)["min"] as Double
    }

    fun getDailyMaxTemperature(dayIndex: Int): Double {
        return (daily[dayIndex]["temp"] as LinkedTreeMap<*, *>)["max"] as Double
    }

    fun getDailyIcon(dayIndex: Int): String {
        return ((daily[dayIndex]["weather"] as ArrayList<*>)[0] as LinkedTreeMap<*, *>)["icon"].toString()
    }

    fun getDailyDayDataTime(dayIndex: Int): Double {
        return daily[dayIndex]["dt"] as Double
    }
}