package com.masjidjalancahaya.kencelenganreminder.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.masjidjalancahaya.kencelenganreminder.model.KencelNotifInfo
import com.masjidjalancahaya.kencelenganreminder.repository.Repository
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.ReminderTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.toNotifInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KencelBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var kencelRepository: Repository

    override fun onReceive(context: Context?, intent: Intent?){
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        val id = intent?.getStringExtra("")
        val service = context?.let { KencelNotificationService(it) }

        coroutineScope.launch {
            val item = id?.let {
                kencelRepository.getKencelById(it)
            }
            item?.collect{model ->
                model.toNotifInfo().let {info ->
                    service?.showNotification(info)
                }
            }

        }
    }
}