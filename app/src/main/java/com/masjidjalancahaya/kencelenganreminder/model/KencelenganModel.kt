package com.masjidjalancahaya.kencelenganreminder.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KencelenganModel(
    var id: String? = null,
    var name: String = "",
    var nomor: Int = 0,
    var address: String = "",
    var lat: String? = null,
    var lang: String? = null
): Parcelable {
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "id" to id,
            "name" to name,
            "nomor" to nomor,
            "address" to address,
            "lat" to lat,
            "lang" to lang
        )
    }
}

