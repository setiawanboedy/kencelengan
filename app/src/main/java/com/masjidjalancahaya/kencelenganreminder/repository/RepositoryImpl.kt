package com.masjidjalancahaya.kencelenganreminder.repository


import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.data.source.remote.FirebaseService
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import com.masjidjalancahaya.kencelenganreminder.notifications.KencelNotificationScheduler
import com.masjidjalancahaya.kencelenganreminder.utils.getFromNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val service: FirebaseService,
    private val scheduler: KencelNotificationScheduler,
): Repository{
    override suspend fun createKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val id = service.createKencelengan(kencelenganModel).await().id
            service.updateKencelengan(id, kencelenganModel).await()
            val kencel = kencelenganModel.copy(id = id)
            scheduleNotification(kencel)
            emit(ResourceState.Success(true))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }

    override suspend fun updateKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val id = kencelenganModel.id
            if (id != null) {
                service.updateKencelengan(id, kencelenganModel).await()
            }
            emit(ResourceState.Success(true))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }

    override suspend fun getKencelById(kencelId: String): Flow<KencelenganModel> = flow {
        try {
            val kencel = service.getKencelById(kencelId).await()
            val kencelengan = kencel.getFromNetwork()
            emit(kencelengan)
        }catch (e: Exception){
            emit(KencelenganModel())
        }
    }

    override suspend fun getAllKencelengan(): Flow<ResourceState<List<KencelenganModel>>> = flow{
        emit(ResourceState.Loading())
        try {
            val getListKencelengan = service.getAllKencelengan().await()

            val listKencelengan = mutableListOf<KencelenganModel>()

            if(getListKencelengan.documents.size != 0){
                listKencelengan.clear()
                for (kencel in getListKencelengan.documents){
                    val kenceleng = kencel.getFromNetwork()
                    listKencelengan.add(kenceleng)
                }
            }
            listKencelengan.forEach{ item ->
                scheduleNotification(item)
            }
            emit(ResourceState.Success(listKencelengan))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }

    override fun deleteKenclengan(kencelId: String): Flow<ResourceState<Boolean>> = flow{
        emit(ResourceState.Loading())
        try {
            service.deleteKencelengan(kencelId).await()
            cancelScheduledNotification(kencelId)
            emit(ResourceState.Success(true))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }

    override suspend fun scheduleAllKencelItemNotifications() {
        getAllKencelengan().collect{kencel ->
            when(kencel){
                is ResourceState.Success ->{
                    kencel.data?.forEach {
                        scheduleNotification(it)
                    }
                }

                else -> {}
            }
        }
    }

    private fun scheduleNotification(kencelenganModel: KencelenganModel){
        kencelenganModel.id?.let {
            kencelenganModel.startDateAndTime?.let { long ->
                scheduler.schedule(
                    kencelId = it,
                    time = long
                )
            }
        }
    }

    private fun cancelScheduledNotification(kencelId: String) {
        scheduler.cancel(kencelId)
    }
}