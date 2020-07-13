package com.fredrikbogg.weatherapp.utils

import com.fredrikbogg.weatherapp.models.WeatherLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

object OpenWeatherAPI {
    private const val API_KEY = "EDIT_THIS"
    private const val baseUrl = "https://api.openweathermap.org/"
    private const val iconURL = "https://openweathermap.org/img/wn/"

    interface OnFetchContents {
        @GET
        fun fetchContents(@Url url: String): Call<WeatherLocation>
    }

    interface OnWeatherLocationRetrievedListener {
        fun onWeatherLocationRetrievedFail(msg: String)
        fun onWeatherLocationRetrieved(weatherLocation: WeatherLocation)
    }

    private fun getDownloadUrl(longitude: String, latitude: String, language: String): String {
        return "${baseUrl}data/2.5/onecall?lat=$latitude&lon=$longitude&lang=$language&appid=$API_KEY"
    }

    fun getWeatherLocation(
        listener: OnWeatherLocationRetrievedListener,
        longitude: String,
        latitude: String,
        language: String
    ) {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val onFetchContents: OnFetchContents = retrofit.create(
            OnFetchContents::class.java
        )

        val req: Call<WeatherLocation> = onFetchContents.fetchContents(
            getDownloadUrl(
                longitude,
                latitude,
                language
            )
        )

        req.enqueue(object : Callback<WeatherLocation> {
            override fun onFailure(call: Call<WeatherLocation>, t: Throwable) {
                listener.onWeatherLocationRetrievedFail("Error retrieving data")
            }

            override fun onResponse(
                call: Call<WeatherLocation>,
                response: Response<WeatherLocation>
            ) {
                val weatherLocation: WeatherLocation? = response.body()
                if (weatherLocation == null) {
                    listener.onWeatherLocationRetrievedFail("Error converting to weather location")
                } else {
                    listener.onWeatherLocationRetrieved(weatherLocation)
                }
            }
        })
    }

    fun getIconURL(icon: String): String {
        return "$iconURL$icon@2x.png"
    }
}