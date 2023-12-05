package com.masjidjalancahaya.kencelenganreminder.utils

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

fun LatLng.convertLatLngToAddress(context: Context): String? {
    val geocoder = Geocoder(context)
    try {
        val addresses = geocoder.getFromLocation(this.latitude, this.longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                return address.getAddressLine(0)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}