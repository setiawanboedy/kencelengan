package com.masjidjalancahaya.kencelenganreminder.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
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

fun LocalDateTime.dateTimeDoubleToDateString():String {
    val date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun LocalDateTime.dateTimeDoubleToTimeString(): String {
    val time = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(time)
}