package com.masjidjalancahaya.kencelenganreminder.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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

fun LocalDate.dateTimeDoubleToDateString():String {
    val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    return this.format(dateFormat)
}

fun LocalTime.dateTimeDoubleToTimeString(): String {
    val dateFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    return this.format(dateFormat)
}

fun LatLng.calculateDistance(start: LatLng): Double {
    val startLatitude = start.latitude
    val startLongitude = start.longitude

    val endLatitude = this.latitude
    val endLongitude = this.longitude

    val results = FloatArray(1)
    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
    return results[0].toDouble() // Distance in meters
}