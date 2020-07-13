package com.fredrikbogg.weatherapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng

object GeocodeHelper {
    fun convertLatitudeAndLongitudeToName(latLng: LatLng, context: Context): String? {
        if (Geocoder.isPresent()) {
            val coder = Geocoder(context)
            val addresses: List<Address>? =
                coder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                return retrieveNameFromAddress(addresses[0])
            }
        }
        return null
    }

    private fun retrieveNameFromAddress(add: Address): String? {
        return listOf<String?>(
            add.subLocality,
            add.locality,
            add.subAdminArea,
            add.adminArea,
            add.countryName
        ).firstOrNull { it != null }
    }
}