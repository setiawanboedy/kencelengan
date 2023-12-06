package com.masjidjalancahaya.kencelenganreminder.notifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import com.masjidjalancahaya.kencelenganreminder.R

class KencelNotificationChannels(
    val context: Context
) {
    fun createKencelNotificationChannels(){
        createAgendaGroup()
        createReminderNotificationChannel()
    }

    private fun createAgendaGroup() {
        val groupId = KencelNotificationService.KENCEL_GROUP_ID
        val groupName = context.getString(R.string.kencel)
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(groupId, groupName)
        )
    }
    private fun createReminderNotificationChannel() {
        val channel = NotificationChannel(
            KencelNotificationService.REMINDER_CHANNEL_ID,
            context.getString(R.string.reminder),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(
            R.string.used_for_string_notification, context.getString(R.string.reminder).lowercase()
        )
        channel.group = KencelNotificationService.KENCEL_GROUP_ID
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}