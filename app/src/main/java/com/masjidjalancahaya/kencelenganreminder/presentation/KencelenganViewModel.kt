package com.masjidjalancahaya.kencelenganreminder.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.repository.Repository
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.ReminderTimeConversion
import com.masjidjalancahaya.kencelenganreminder.worker.WorkKencel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class KencelenganViewModel @Inject constructor(
    private val repository: Repository,
    workManager: WorkManager,
    private val reminderTimeConversion: ReminderTimeConversion,
    private val dateTimeConversion: DateTimeConversion
): ViewModel() {

    private var _allKencelengan = MutableLiveData<ResourceState<List<KencelenganModel>>>()

    val allKencelengan: LiveData<ResourceState<List<KencelenganModel>>> get() = _allKencelengan

    private var _createKencelengan = MutableLiveData<ResourceState<Boolean>>()
    private var _updateKencelengan = MutableLiveData<ResourceState<Boolean>>()
    private var _deleteKencelengan = MutableLiveData<ResourceState<Boolean>>()

    val isCreateKencelengan: LiveData<ResourceState<Boolean>> get() = _createKencelengan
    val isDeleteKencelengan: LiveData<ResourceState<Boolean>> get() = _deleteKencelengan
    val isUpdateKencelengan: LiveData<ResourceState<Boolean>> get() = _updateKencelengan

    private val _selectedStartDate = MutableStateFlow(LocalDate.now())
    private val selectedStartDate = _selectedStartDate.asStateFlow()
    fun setStartDate(selectedStartDate: LocalDate) {
        _selectedStartDate.value = selectedStartDate
    }

    private val _selectedStartTime = MutableStateFlow(LocalTime.now())
    private val selectedStartTime = _selectedStartTime.asStateFlow()
    fun setStartTime(selectedStartTime: LocalTime) {
        _selectedStartTime.value = selectedStartTime
    }

    private var _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            workManager.apply {
                enqueue(syncFullKencelWorkRequest)
            }
        }
    }

    fun getKencelengans() {
        viewModelScope.launch {
            val list = repository.getAllKencelengan()
            list.collect{
                _allKencelengan.value =it

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

    fun createKencelengan(kencelenganModel: KencelenganModel){

        viewModelScope.launch {

            val kencel = kencelenganModel.copy(
                startDateAndTime = reminderTimeConversion.toZonedEpochMilli(
                    startLocalDateTime = LocalDateTime.of(
                        selectedStartDate.value,
                        selectedStartTime.value
                    ),
                    dateTimeConversion = dateTimeConversion
                ),
            )
            val create = repository.createKencelengan(kencel)
            create.collect{
                _createKencelengan.postValue(it)
            }
        }
    }

    fun updateKencelengan(kencelenganModel: KencelenganModel){

        viewModelScope.launch {
            val kencel = kencelenganModel.copy(
                startDateAndTime = reminderTimeConversion.toZonedEpochMilli(
                    startLocalDateTime = LocalDateTime.of(
                        selectedStartDate.value,
                        selectedStartTime.value
                    ),
                    dateTimeConversion = dateTimeConversion
                ) ?: kencelenganModel.startDateAndTime,
            )

            val update = repository.updateKencelengan(kencel)
            update.collect{
                _updateKencelengan.postValue(it)
            }
        }
    }

    fun deleteKencelengan(kencelId: String){
        viewModelScope.launch {
            val delete = repository.deleteKenclengan(kencelId)
            delete.collect{
                _deleteKencelengan.postValue(it)
            }
        }
    }

    private val syncFullKencelWorkRequest =
        PeriodicWorkRequest.Builder(WorkKencel::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()


    fun setDonateName(name: String){
        _uiState.value = _uiState.value?.copy(
            donateName = name
        )
    }
    fun setNoHp(phone: Int){
        _uiState.value = _uiState.value?.copy(
            noHp = phone
        )
    }

    data class UiState(
        val donateName: String? = null,
        val noHp: Int? = null
    )

}