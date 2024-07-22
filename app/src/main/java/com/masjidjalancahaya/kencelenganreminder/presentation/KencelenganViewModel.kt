package com.masjidjalancahaya.kencelenganreminder.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.repository.Repository
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class KencelenganViewModel @Inject constructor(
    private val repository: Repository,
    private val dateTimeConversion: DateTimeConversion
): ViewModel() {

    private var _allKencelengan = MutableLiveData<ResourceState<List<KencelenganModel>>>()

    val allKencelengan: LiveData<ResourceState<List<KencelenganModel>>> get() = _allKencelengan


    fun getKencelengans() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllKencelengan()
            list.collect{
                    _allKencelengan.postValue(it)

            }
        }

    }

    fun swipeRefreshKencel(){
        viewModelScope.launch {
            val list = repository.getAllKencelengan()
            val dateTimeNow = LocalDateTime.now()
            list.collect{
                _allKencelengan.value = it
                it.data?.forEach {data ->
                    val specificDateTime = dateTimeConversion.zonedEpochMilliToLocalDateTime(data.startDateAndTime!!)
                    if (specificDateTime.isBefore(dateTimeNow)){
                        val update = data.copy(isBlue = true)
                        repository.updateKencelengan(update).collect()
                    }else{
                        val update = data.copy(isBlue = false)
                        repository.updateKencelengan(update).collect()
                    }
                }
            }
        }
    }

//    private val syncFullKencelWorkRequest =
//        PeriodicWorkRequest.Builder(WorkKencel::class.java, 15, TimeUnit.MINUTES)
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//            ).build()
//
//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            workManager.apply {
//                enqueue(syncFullKencelWorkRequest)
//            }
//        }
//    }


}