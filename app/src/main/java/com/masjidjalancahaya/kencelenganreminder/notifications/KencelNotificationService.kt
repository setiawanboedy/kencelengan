package com.masjidjalancahaya.kencelenganreminder.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.data.model.KencelNotifInfo
import com.masjidjalancahaya.kencelenganreminder.presentation.main.MainActivity

class KencelNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val intent = Intent(context, MainActivity::class.java)
    private val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    fun showNotification(info: KencelNotifInfo){
        val notification = NotificationCompat.Builder(context, info.notificationChannel)
            .setSmallIcon(R.drawable.ic_small_icon)
            .setContentTitle("Kencelengan penuh dari ${info.title}")
            .setContentText(info.description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(info.id.hashCode(), notification.build())
    }

    companion object {
        const val KENCEL_GROUP_ID = "kencel_group"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
    }
}