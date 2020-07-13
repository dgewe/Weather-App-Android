package com.fredrikbogg.weatherapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.fredrikbogg.weatherapp.models.SelectedLocation
import com.google.gson.Gson

object SharedPreferencesHelper {
    private const val KEY_SELECTED_LOCATION = "selected_location"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("com.fredrikbogg.weatherapp", Context.MODE_PRIVATE)
    }

    fun getSelectedLocation(context: Context): SelectedLocation? {
        val json: String? = getPrefs(context).getString(KEY_SELECTED_LOCATION, "")
        return Gson().fromJson(json, SelectedLocation::class.java)
    }

    fun saveSelectedLocation(context: Context, selectedLocation: SelectedLocation) {
        val json = Gson().toJson(selectedLocation)
        getPrefs(context).edit().putString(KEY_SELECTED_LOCATION, json).apply()
    }
}