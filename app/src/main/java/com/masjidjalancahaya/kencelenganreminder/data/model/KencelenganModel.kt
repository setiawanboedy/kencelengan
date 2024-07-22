package com.masjidjalancahaya.kencelenganreminder.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class KencelenganModel(
    var id: String? = "",
    var name: String? = null,
    var nomor: Long? = 0,
    var address: String? = "",
    var isBlue: Boolean? = false,
    var distance: Double? = null,
    var startDateAndTime: Long? = null,
    var lat: Double? = null,
    var lang: Double? = null
): Parcelable {
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "id" to id,
            "name" to name,
            "nomor" to nomor,
            "address" to address,
            "isBlue" to isBlue,
            "startDateAndTime" to startDateAndTime,
            "lat" to lat,
            "lang" to lang
        )
    }
}

