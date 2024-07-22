package com.masjidjalancahaya.kencelenganreminder.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.masjidjalancahaya.kencelenganreminder.data.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class BootupNotificationBroadcastReceiver: BroadcastReceiver() {
    @Inject
    lateinit var repository: Repository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                repository.scheduleAllKencelItemNotifications()
            }
        }
    }
}