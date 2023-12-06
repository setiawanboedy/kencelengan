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
        coroutineScope.launch {
            val item = id?.let {
                kencelRepository.getKencelById(it)
            }

            item?.collect{model ->
                model.toNotifInfo().let {info ->
                    val update = model.copy(isBlue = true)
                    kencelRepository.updateKencelengan(update)
                    Timber.tag("model").d(update.toString())
                    service?.showNotification(info)
                }
            }
        }
    }
}