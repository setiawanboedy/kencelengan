package com.masjidjalancahaya.kencelenganreminder.presentation.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.data.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.presentation.add.AddActivity.Companion.DATA_KEY
import com.masjidjalancahaya.kencelenganreminder.data.repository.Repository
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.ReminderTimeConversion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    private val reminderTimeConversion: ReminderTimeConversion,
    private val dateTimeConversion: DateTimeConversion
) : ViewModel() {

    private var _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState

    init {
        getDataArgs()
    }

    private fun getDataArgs() {
        val data = savedStateHandle.get<KencelenganModel?>(key = DATA_KEY)
        if (data != null) {
            val localDateTime =
                dateTimeConversion.zonedEpochMilliToLocalDateTime(data.startDateAndTime!!)
            setId(id = data.id!!)
            setDonateName(name = data.name!!)
            setNoHp(phone = data.nomor!!.toString())
            setAddress(address = data.address!!)
            setStartDate(date = localDateTime.toLocalDate())
            setStartTime(time = localDateTime.toLocalTime())
            setLatLng(latLng = LatLng(data.lat!!, data.lang!!))
            setColorBlue(isBlue = data.isBlue!!)
        }
    }

    private fun setId(id: String) {
        _uiState.value = _uiState.value?.copy(
            id = id
        )
    }

    fun setDonateName(name: String) {
        val newState = _uiState.value?.copy(
            donateName = name
        )
        _uiState.value = newState?.copy(isDataValid = isFormValid(newState))
    }

    fun setNoHp(phone: String) {
        val newState = _uiState.value?.copy(
            phone = phone
        )
        _uiState.value = newState?.copy(isDataValid = isFormValid(newState))
    }

    fun setAddress(address: String) {
        val newState = _uiState.value?.copy(
            address = address
        )
        _uiState.value = newState?.copy(isDataValid = isFormValid(newState))
    }

    fun setStartDate(date: LocalDate) {
        val newState = _uiState.value?.copy(
            startDate = date
        )
        _uiState.value = newState?.copy(isDataValid = isFormValid(newState))
    }

    fun setStartTime(time: LocalTime) {
        val newState = _uiState.value?.copy(
            startTime = time
        )
        _uiState.value = newState?.copy(isDataValid = isFormValid(newState))
    }

    fun setLatLng(latLng: LatLng?) {
        val newState = _uiState.value?.copy(
            latLng = latLng
        )
        _uiState.value = newState?.copy(isDataValid = isFormValid(newState))
    }

    private fun setRedirect(redirect: Boolean) {
        _uiState.value = _uiState.value?.copy(redirect = redirect)
    }

    private fun setColorBlue(isBlue: Boolean) {
        _uiState.value = _uiState.value?.copy(isBlue = isBlue)
    }

    private fun isFormValid(state: UiState?): Boolean {
        return state?.donateName?.isNotEmpty() == true &&
                state.phone?.isNotEmpty() == true &&
                state.address?.isNotEmpty() == true &&
                state.startDate != null &&
                state.startTime != null
    }

    fun submit() {
        viewModelScope.launch(Dispatchers.IO) {
            val startDateAndTimeToLong = reminderTimeConversion.toZonedEpochMilli(
                dateTimeConversion = dateTimeConversion,
                startLocalDateTime = LocalDateTime.of(
                    uiState.value?.startDate,
                    uiState.value?.startTime
                )
            )
            if (uiState.value != null) {
                update(startDateAndTimeToLong)
            }else{
                save(startDateAndTimeToLong)
            }
        }
    }

    private suspend fun save(startDateAndTimeToLong: Long?) {
        if (uiState.value != null) {
            val create = repository.createKencelengan(kencelenganModel = KencelenganModel().apply {
                name = uiState.value?.donateName
                nomor = uiState.value?.phone?.toLong()
                address = uiState.value?.address
                startDateAndTime = startDateAndTimeToLong
                lat = uiState.value?.latLng?.latitude
                lang = uiState.value?.latLng?.longitude
            })
            create.collect {
                if (it is ResourceState.Success) {
                    withContext(Dispatchers.Main) {
                        setRedirect(redirect = true)
                    }
                } else if (it is ResourceState.Error) {
                    withContext(Dispatchers.Main) {
                        setRedirect(redirect = false)
                    }
                }
            }
        }
    }

    private suspend fun update(startDateAndTimeToLong: Long?) {
        if (uiState.value?.id != null) {
            val create = repository.updateKencelengan(
                kencelenganModel = KencelenganModel().apply {
                id = uiState.value?.id
                name = uiState.value?.donateName
                nomor = uiState.value?.phone?.toLong()
                address = uiState.value?.address
                startDateAndTime = startDateAndTimeToLong
                lat = uiState.value?.latLng?.latitude
                lang = uiState.value?.latLng?.longitude
            })
            create.collect {
                if (it is ResourceState.Success) {
                    withContext(Dispatchers.Main) {
                        setRedirect(redirect = true)
                    }
                } else if (it is ResourceState.Error) {
                    withContext(Dispatchers.Main) {
                        setRedirect(redirect = false)
                    }
                }
            }
        }
    }

    fun deleteItem(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteKenclengan(id).collect{
                if (it is ResourceState.Success) {
                    withContext(Dispatchers.Main) {
                        setRedirect(redirect = true)
                    }
                } else if (it is ResourceState.Error) {
                    withContext(Dispatchers.Main) {
                        setRedirect(redirect = false)
                    }
                }
            }
        }
    }

    data class UiState(
        val id: String? = null,
        val donateName: String? = null,
        val phone: String? = null,
        val address: String? = null,
        val startDate: LocalDate? = null,
        val startTime: LocalTime? = null,
        val latLng: LatLng? = null,
        val isDataValid: Boolean = false,
        val redirect: Boolean = false,
        val isBlue: Boolean = false
    )
}