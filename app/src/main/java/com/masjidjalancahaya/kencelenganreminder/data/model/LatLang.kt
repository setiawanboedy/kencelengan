package com.masjidjalancahaya.kencelenganreminder.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LatLang(
    var lat: Double,
    var lang: Double
): Parcelable
