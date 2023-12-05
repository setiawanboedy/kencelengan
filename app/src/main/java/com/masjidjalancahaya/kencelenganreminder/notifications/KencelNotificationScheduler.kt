package com.masjidjalancahaya.kencelenganreminder.notifications

interface KencelNotificationScheduler {
    fun schedule(kencelId: String, time: Long)
    fun cancel(kencelId: String)
}