package com.fredrikbogg.weatherapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

object GooglePlacesAPI {
    private const val API_KEY = "EDIT_THIS"
    private const val placeRequestCode = 1
    private val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)

    interface OnGooglePlacesRetrievedListener {
        fun onGooglePlaceRetrievedFail(msg: String)
        fun onGooglePlaceRetrieved(name: String, latLng: LatLng)
    }

    private fun initPlaces(context: Context) {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, API_KEY)
        }
    }

    fun retrievePlaces(listener: OnGooglePlacesRetrievedListener) {
        initPlaces(listener as Context)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            placeFields
        )
            .build(listener as Context)
        (listener as Activity).startActivityForResult(
            intent,
            placeRequestCode
        )
    }

    fun handlePlacesOnActivityResult(
        listener: OnGooglePlacesRetrievedListener,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == placeRequestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        val name = place.name
                        val latLng = place.latLng
                        if (name != null && latLng != null) {
                            listener.onGooglePlaceRetrieved(name, latLng)
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    listener.onGooglePlaceRetrievedFail("Failed retrieving place")
                }
                Activity.RESULT_CANCELED -> {
                }
            }
        }
    }
}