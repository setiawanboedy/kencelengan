package com.masjidjalancahaya.kencelenganreminder.repository


import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.data.source.remote.FirebaseService
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.masjidjalancahaya.kencelenganreminder.notifications.KencelNotificationScheduler
import com.masjidjalancahaya.kencelenganreminder.utils.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.ReminderTimeConversion
import timber.log.Timber
import java.time.LocalDateTime

class RepositoryImpl @Inject constructor(
    private val service: FirebaseService,
    private val scheduler: KencelNotificationScheduler,
): Repository{
    override suspend fun createKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val id = service.createKencelengan(kencelenganModel).await().id
            service.updateKencelengan(id, kencelenganModel).await()
            val kencel = KencelenganModel(
                id = id,
                name = kencelenganModel.name,
                nomor = kencelenganModel.nomor,
                address = kencelenganModel.address,
                startDateAndTime = kencelenganModel.startDateAndTime,
                lat = kencelenganModel.lat,
                lang = kencelenganModel.lang
            )
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

    override fun getKencelById(kencelId: String): Flow<KencelenganModel> = flow {
        try {
            val kencel = service.getKencelById(kencelId).await()
            val kencelengan = KencelenganModel(
                id = kencel.get("id").toString(),
                name = kencel.get("name").toString(),
                nomor = kencel.get("nomor").toString().toInt(),
                address = kencel.get("address").toString(),
                startDateAndTime = kencel.get("startDateAndTime").toString().toLong()
            )
            emit(kencelengan)
        }catch (e: Exception){
            val kencelengan = KencelenganModel()
            emit(kencelengan)
        }
    }

    override fun getAllKencelengan(): Flow<ResourceState<List<KencelenganModel>>> = flow{
        emit(ResourceState.Loading())
        try {
            val getListKencelengan = service.getAllKencelengan().await()

            val listKencelengan = mutableListOf<KencelenganModel>()

            if(getListKencelengan.documents.size != 0){
                listKencelengan.clear()
                for (celeng in getListKencelengan.documents){
                    val kenceleng = KencelenganModel(
                        id = celeng.get("id").toString(),
                        name = celeng.get("name").toString(),
                        nomor = celeng.get("nomor").toString().toInt(),
                        address = celeng.get("address").toString(),
                        startDateAndTime = celeng.get("startDateAndTime").toString().toLong()
                    )

                    listKencelengan.add(kenceleng)
                }
            }
//            listKencelengan.forEach{ item ->
//                scheduleNotification(item)
//            }
            emit(ResourceState.Success(listKencelengan))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
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