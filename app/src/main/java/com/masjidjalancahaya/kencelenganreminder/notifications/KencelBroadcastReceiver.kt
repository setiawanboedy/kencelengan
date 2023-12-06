package com.masjidjalancahaya.kencelenganreminder.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.masjidjalancahaya.kencelenganreminder.notifications.KencelNotificationSchedulerImpl.Companion.KENCEL_NOTIF_SCHED_INTENT
import com.masjidjalancahaya.kencelenganreminder.repository.Repository
import com.masjidjalancahaya.kencelenganreminder.utils.toNotifInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class KencelBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var kencelRepository: Repository

    override fun onReceive(context: Context?, intent: Intent?){
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        val id = intent?.getStringExtra(KENCEL_NOTIF_SCHED_INTENT)
        val service = context?.let { KencelNotificationService(it) }
        if (id != null){
            coroutineScope.launch {
                val item = kencelRepository.getKencelById(id)

                item.collect{ model ->
                    val update = model.copy(isBlue = true)
                    kencelRepository.updateKencelengan(update).collect{}
                    Timber.tag("model").d(update.toString())
                    model.toNotifInfo().let {info ->

                        service?.showNotification(info)
                    }
                }
            }
        }
    }
}