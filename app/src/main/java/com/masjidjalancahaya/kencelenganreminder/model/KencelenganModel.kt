package com.masjidjalancahaya.kencelenganreminder.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class KencelenganModel(
    var id: String? = "",
    var name: String? = null,
    var nomor: Int? = 0,
    var address: String? = "",
    var startDateAndTime: Long? = 0,
    var lat: String? = null,
    var lang: String? = null
): Parcelable {
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "id" to id,
            "name" to name,
            "nomor" to nomor,
            "address" to address,
            "startDateAndTime" to startDateAndTime,
            "lat" to lat,
            "lang" to lang
        )
    }
}

