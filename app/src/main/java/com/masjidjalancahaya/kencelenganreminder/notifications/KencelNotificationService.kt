package com.masjidjalancahaya.kencelenganreminder.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.model.KencelNotifInfo

class KencelNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(info: KencelNotifInfo){
        val notification = NotificationCompat.Builder(context, info.notificationChannel)
            .setSmallIcon(R.drawable.ic_setting)
            .setContentTitle(info.title)
            .setContentText(info.description)
            .setAutoCancel(true)

        notificationManager.notify(info.id.hashCode(), notification.build())
    }

    companion object {
        const val KENCEL_GROUP_ID = "kencel_group"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
    }
}