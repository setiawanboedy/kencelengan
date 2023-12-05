package com.masjidjalancahaya.kencelenganreminder.model

data class KencelNotifInfo(
    val title: String,
    val description: String,
    val id: String,
    val notificationChannel: String,
    val notificationZonedMilliTime: Long
)
