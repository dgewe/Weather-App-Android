package com.fredrikbogg.weatherapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*

object GoogleLocationAPI {
    private const val permissionLocationRequestCode = 1

    interface OnCurrentLocationRetrievedListener {
        fun onCurrentLocationRetrievedFail(msg: String)
        fun onCurrentLocationRetrieved(location: Location)
    }

    private fun checkIfServicesAvailable(context: Context): Boolean {
        val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }

    private fun getLocationCallback(listener: OnCurrentLocationRetrievedListener): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult != null) {
                    for (location in locationResult.locations) {
                        listener.onCurrentLocationRetrieved(location)
                    }
                } else {
                    listener.onCurrentLocationRetrievedFail("No location result")
                }
                LocationServices.getFusedLocationProviderClient(listener as Context)
                    .removeLocationUpdates(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadLocation(listener: OnCurrentLocationRetrievedListener) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(listener as Context)
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            getLocationCallback(
                listener
            ),
            Looper.getMainLooper()
        )
    }

    fun getLocation(listener: OnCurrentLocationRetrievedListener) {
        if (!checkIfServicesAvailable(
                listener as Context
            )
        ) {
            listener.onCurrentLocationRetrievedFail("Google location services not available")
        } else {
            val permission = ContextCompat.checkSelfPermission(
                listener as Context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    listener as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    permissionLocationRequestCode
                )
            } else {
                loadLocation(
                    listener
                )
            }
        }
    }

    fun handleLocationPermissionResult(
        listener: OnCurrentLocationRetrievedListener,
        requestCode: Int, grantResults: IntArray
    ) {
        if (requestCode == permissionLocationRequestCode) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                listener.onCurrentLocationRetrievedFail("Failed retrieving permission for current location")
            } else {
                loadLocation(
                    listener
                )
            }
        }
    }
}
