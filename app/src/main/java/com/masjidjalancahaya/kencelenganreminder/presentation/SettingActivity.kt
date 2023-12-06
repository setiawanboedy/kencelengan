package com.masjidjalancahaya.kencelenganreminder.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.masjidjalancahaya.kencelenganreminder.R
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivitySettingBinding
import com.masjidjalancahaya.kencelenganreminder.worker.WorkKencel
import java.util.concurrent.TimeUnit

class SettingActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        workManager = WorkManager.getInstance(this)

        binding.btnPeriodicTask.setOnClickListener {
//            startPeriodicTask()
        }

        binding.btnCancelTask.setOnClickListener {
//            cancelPeriodicTask()
        }

    }

//    @SuppressLint("InvalidPeriodicWorkRequestInterval")
//    private fun startPeriodicTask() {
//        val data = Data.Builder()
//            .putString("", "")
//            .build()
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//        periodicWorkRequest = PeriodicWorkRequest.Builder(WorkKencel::class.java, 1, TimeUnit.MINUTES)
//            .setInputData(data)
//            .setConstraints(constraints)
//            .build()
//        workManager.enqueue(periodicWorkRequest)
//        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
//            .observe(this@SettingActivity) { workInfo ->
//                val status = workInfo.state.name
//                binding.textStatus.append("\n" + status)
//                binding.btnCancelTask.isEnabled = false
//                if (workInfo.state == WorkInfo.State.ENQUEUED) {
//                    binding.btnCancelTask.isEnabled = true
//                }
//            }
//    }
//
//    private fun cancelPeriodicTask() {
//        workManager.cancelWorkById(periodicWorkRequest.id)
//    }

}