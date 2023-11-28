package com.masjidjalancahaya.kencelenganreminder.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class KencelenganViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private var _allKencelengan = MutableLiveData<ResourceState<List<KencelenganModel>>>()

    val allKencelengan: LiveData<ResourceState<List<KencelenganModel>>> get() = _allKencelengan

    private var _createKencelengan = MutableLiveData<ResourceState<Boolean>>()

    val isCreateKencelengan: LiveData<ResourceState<Boolean>> get() = _createKencelengan

    fun getKencelengans() {
        val list = repository.getAllKencelengan()
        viewModelScope.launch {
            list.collect{
                _allKencelengan.postValue(it)
            }
        }

    }

    fun createKencelengan(kencelenganModel: KencelenganModel){

        viewModelScope.launch {
            val create = repository.createKencelengan(kencelenganModel)
            create.collect{
                _createKencelengan.postValue(it)
            }
        }
    }
}