package com.masjidjalancahaya.kencelenganreminder.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.time.ZonedDateTime

class KencelNotificationSchedulerImpl(
    private val context: Context
): KencelNotificationScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(kencelId: String, time: Long) {
        if (time < ZonedDateTime.now().toInstant().toEpochMilli()) return
        val intent = Intent(context, KencelBroadcastReceiver::class.java)
            .putExtra(KENCEL_NOTIF_SCHED_INTENT, kencelId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            kencelId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    override fun cancel(kencelId: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                kencelId.hashCode(),
                Intent(context, KencelNotificationService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    companion object {
        const val KENCEL_NOTIF_SCHED_INTENT = "KENCEL_NOTIF_SCHED_INTENT"
    }
}