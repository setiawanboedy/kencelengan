package com.masjidjalancahaya.kencelenganreminder.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.model.KencelNotifInfo
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.notifications.KencelNotificationService

fun KencelenganModel.toNotifInfo(): KencelNotifInfo{


    val channel = KencelNotificationService.REMINDER_CHANNEL_ID

    return KencelNotifInfo(
        title = name ?: "",
        description = address ?: "",
        id = id ?: "",
        isBlue = isBlue ?: false,
        notificationChannel = channel,
        notificationZonedMilliTime = startDateAndTime ?: 0
    )
}

fun DocumentSnapshot.getFromNetwork(): KencelenganModel{
    return KencelenganModel(
        id = this.get("id").toString(),
        name = this.get("name").toString(),
        nomor = this.get("nomor").toString().toLong(),
        address = this.get("address").toString(),
        isBlue = this.get("isBlue").toString().toBoolean(),
        startDateAndTime = this.get("startDateAndTime").toString().toLong(),
        lat = this.get("lat").toString().toDouble(),
        lang = this.get("lang").toString().toDouble()
    )
}