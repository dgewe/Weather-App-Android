package com.fredrikbogg.weatherapp.utils

import kotlin.math.roundToInt

object TemperatureConverter {
    fun convertKelvinToCelsius(kelvin: Double): Int {
        return (kelvin - 273.15).roundToInt()
    }
}