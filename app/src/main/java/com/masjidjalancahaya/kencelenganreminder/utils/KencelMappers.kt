package com.masjidjalancahaya.kencelenganreminder.utils

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
        notificationChannel = channel,
        notificationZonedMilliTime = startDateAndTime ?: 0
    )
}