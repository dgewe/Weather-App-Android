package com.fredrikbogg.weatherapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object TimeAndDateConverter {
    fun getTime(context: Context, epochSeconds: Double, timeZoneOffset: String): String {
        val date = Date(epochSeconds.toLong() * 1000) // Convert to milliseconds

        val pattern = if (!DateFormat.is24HourFormat(context)) {
            "h aa"
        } else {
            "HH"
        }

        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val timeZoneInHours: Int = timeZoneOffset.toInt() / 3600

        if (timeZoneInHours >= 0) {
            formatter.timeZone = TimeZone.getTimeZone("GMT+$timeZoneInHours") // 'GMT+X'
        } else {
            formatter.timeZone = TimeZone.getTimeZone("GMT$timeZoneInHours") // 'GMT-X'
        }
        return formatter.format(date)
    }

    fun getWeekday(epochSeconds: Double): String {
        val date = Date(epochSeconds.toLong() * 1000) // Convert to milliseconds
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateAndTime(): String? {
        val pat = SimpleDateFormat().toLocalizedPattern().replace("\\W?[Yy]+\\W?".toRegex(), " ")
        val formatter = SimpleDateFormat(pat, Locale.getDefault())
        return formatter.format(Date())
    }
}