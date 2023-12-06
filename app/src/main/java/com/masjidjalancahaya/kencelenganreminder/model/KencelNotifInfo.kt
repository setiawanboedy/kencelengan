package com.masjidjalancahaya.kencelenganreminder.model

data class KencelNotifInfo(
    var title: String,
    var description: String,
    var id: String,
    var isBlue: Boolean,
    var notificationChannel: String,
    var notificationZonedMilliTime: Long
)
