package com.fredrikbogg.weatherapp.activities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fredrikbogg.weatherapp.R
import com.fredrikbogg.weatherapp.adapters.DailyWeatherListAdapter
import com.fredrikbogg.weatherapp.adapters.HourlyWeatherListAdapter
import com.fredrikbogg.weatherapp.handlers.*
import com.fredrikbogg.weatherapp.models.*
import com.fredrikbogg.weatherapp.utils.*
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OpenWeatherAPI.OnWeatherLocationRetrievedListener,
    GoogleLocationAPI.OnCurrentLocationRetrievedListener,
    GooglePlacesAPI.OnGooglePlacesRetrievedListener {

    private val progressDialogHandler: ProgressDialogHandler = ProgressDialogHandler()
    private lateinit var lastUpdatedTextView: TextView
    private lateinit var refreshButton: ImageButton
    private var selectedLocation: SelectedLocation? = null
    private lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lastUpdatedTextView = findViewById(R.id.lastUpdatedTextView)
        mainLayout = findViewById(R.id.MainLayout)
        refreshButton = findViewById<ImageButton>(R.id.updateWeatherImageButton).apply {
            setOnClickListener { updateWeather() }
        }
        findViewById<ImageButton>(R.id.changeLocationImageButton).apply {
            setOnClickListener { changeLocation() }
        }
        selectedLocation = SharedPreferencesHelper.getSelectedLocation(this)
        val latLng = selectedLocation?.latLng
        if (latLng != null) updateWeather() else GoogleLocationAPI.getLocation(this)
    }

    private fun changeLocation() {
        GooglePlacesAPI.retrievePlaces(this)
    }

    private fun updateWeather() {
        val latLng = selectedLocation?.latLng
        if (latLng != null) {
            setLoading(true)
            getWeatherFromAPI(latLng)
        } else {
            throw Exception("Invalid selected location")
        }
    }

    private fun setLoading(isLoading: Boolean) {
        progressDialogHandler.toggleProgressDialog(this, isLoading)
    }

    private fun weatherUpdated() {
        if (refreshButton.visibility != View.VISIBLE) {
            refreshButton.visibility = View.VISIBLE
        }
        val updatedText = "${TimeAndDateConverter.getCurrentDateAndTime()}"
        lastUpdatedTextView.text = updatedText
        Toast.makeText(this, "Weather updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateCurrentWeather(wL: WeatherLocation) {
        val name = selectedLocation?.name ?: "Not found"
        val description = wL.getCurrentDescription()
        val temp =
            TemperatureConverter.convertKelvinToCelsius(wL.getCurrentTemperature()).toString()
        val minTemp = TemperatureConverter.convertKelvinToCelsius(wL.getDailyMinTemperature(0))
            .toString()
        val maxTemp = TemperatureConverter.convertKelvinToCelsius(wL.getDailyMaxTemperature(0))
            .toString()
        val iconURL = OpenWeatherAPI.getIconURL(wL.getCurrentIcon())
        val currentWeather = CurrentWeather(name, description, temp, minTemp, maxTemp, iconURL)

        Picasso.get().load(currentWeather.iconURL)
            .into(findViewById<ImageView>(R.id.weatherTypeImageView))
        findViewById<TextView>(R.id.cityTextView).text = currentWeather.city
        findViewById<TextView>(R.id.currentTemperatureTextView).text = currentWeather.temp
        findViewById<TextView>(R.id.weatherDescriptionTextView).text = currentWeather.description
        findViewById<TextView>(R.id.maxTemperatureTextView).text = currentWeather.maxTemp
        findViewById<TextView>(R.id.minTemperatureTextView).text = currentWeather.minTemp
    }

    private fun updateHourlyWeather(wL: WeatherLocation) {
        val hourlyWeatherList = ArrayList<HourlyWeather>()

        for (hour in 0 until 25) {
            val time =
                TimeAndDateConverter.getTime(this, wL.getHourlyDataTime(hour), wL.timezoneOffset)
            val iconURL = OpenWeatherAPI.getIconURL(wL.getHourlyIcon(hour))
            val temp = TemperatureConverter.convertKelvinToCelsius(wL.getHourlyTemperature(hour))
            val weather = HourlyWeather(time, temp.toString(), iconURL)
            hourlyWeatherList.add(weather)
        }
        val hourlyViewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val hourlyViewAdapter = HourlyWeatherListAdapter(hourlyWeatherList)
        findViewById<RecyclerView>(R.id.hourlyRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = hourlyViewManager
            adapter = hourlyViewAdapter
        }
    }

    private fun updateDailyWeather(wL: WeatherLocation) {
        val dailyWeatherList = ArrayList<DailyWeather>()
        for (day in 1 until 8) {
            val dayOfWeek = TimeAndDateConverter.getWeekday(wL.getDailyDayDataTime(day))
            val iconURL = OpenWeatherAPI.getIconURL(wL.getDailyIcon(day))
            val maxTemp =
                TemperatureConverter.convertKelvinToCelsius(wL.getDailyMaxTemperature(day))
            val minTemp =
                TemperatureConverter.convertKelvinToCelsius(wL.getDailyMinTemperature(day))
            val weather = DailyWeather(dayOfWeek, iconURL, maxTemp.toString(), minTemp.toString())
            dailyWeatherList.add(weather)
        }
        val hourlyViewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val hourlyViewAdapter = DailyWeatherListAdapter(dailyWeatherList)
        findViewById<RecyclerView>(R.id.dailyRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = hourlyViewManager
            adapter = hourlyViewAdapter
        }
    }

    private fun getWeatherFromAPI(latLng: LatLng) {
        OpenWeatherAPI.getWeatherLocation(
            this,
            latitude = latLng.latitude.toString(),
            longitude = latLng.longitude.toString(),
            language = Locale.getDefault().language
        )
    }

    private fun updateSelectedLocation(name: String?, latLng: LatLng) {
        selectedLocation = name?.let { SelectedLocation(it, latLng) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        GoogleLocationAPI.handleLocationPermissionResult(this, requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        GooglePlacesAPI.handlePlacesOnActivityResult(this, requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onWeatherLocationRetrievedFail(msg: String) {
        setLoading(false)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onWeatherLocationRetrieved(weatherLocation: WeatherLocation) {
        selectedLocation?.let { SharedPreferencesHelper.saveSelectedLocation(this, it) }
        updateCurrentWeather(weatherLocation)
        updateHourlyWeather(weatherLocation)
        updateDailyWeather(weatherLocation)
        weatherUpdated()
        setLoading(false)
        if (mainLayout.visibility != View.VISIBLE) {
            mainLayout.visibility = View.VISIBLE
        }
    }

    override fun onGooglePlaceRetrievedFail(msg: String) {
        setLoading(false)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onGooglePlaceRetrieved(name: String, latLng: LatLng) {
        updateSelectedLocation(name, latLng)
        updateWeather()
    }

    override fun onCurrentLocationRetrievedFail(msg: String) {
        setLoading(false)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCurrentLocationRetrieved(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val name = GeocodeHelper.convertLatitudeAndLongitudeToName(latLng, this)
        updateSelectedLocation(name, latLng)
        updateWeather()
    }
}