package com.masjidjalancahaya.kencelenganreminder.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.masjidjalancahaya.kencelenganreminder.data.repository.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber


@HiltWorker
class WorkKencel @AssistedInject constructor (
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val repository: Repository
): CoroutineWorker(appContext = appContext, params = workerParams) {


    companion object {
        private val TAG = WorkKencel::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "kencelengan channel"
    }

    override suspend fun doWork(): Result {
//        showNotification("Berhasil", "Yey, Work Manager Berhasil")

        repository.getAllKencelengan()
        return getResult()
    }

    private fun getResult(): Result {
        return try {
            Result.success()
        }catch (e: Exception){
            Timber.tag("work").d("not working")
            Result.failure()
        }

    }

    @SuppressLint("ServiceCast")
    private fun showNotification(title: String, description: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(androidx.core.R.drawable.notification_action_background)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notification.setChannelId(CHANNEL_ID)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}