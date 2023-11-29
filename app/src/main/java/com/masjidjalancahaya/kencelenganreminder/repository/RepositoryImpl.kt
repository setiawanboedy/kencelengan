package com.masjidjalancahaya.kencelenganreminder.repository


import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.data.source.remote.FirebaseService
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.firestore.ktx.toObject

class RepositoryImpl @Inject constructor(
    private val service: FirebaseService
): Repository{
    override suspend fun createKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val id = service.createKencelengan(kencelenganModel).await().id
            service.upateKencelengan(id, kencelenganModel).await()
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
                service.upateKencelengan(id, kencelenganModel).await()
            }
            emit(ResourceState.Success(true))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
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
                        address = celeng.get("address").toString()
                    )

                    listKencelengan.add(kenceleng)
                }
            }
            emit(ResourceState.Success(listKencelengan))
        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }
}