package com.masjidjalancahaya.kencelenganreminder.repository

import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import kotlinx.coroutines.flow.Flow


interface Repository {
    suspend fun createKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>>

    suspend fun updateKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>>

    suspend fun getKencelById(kencelId: String): Flow<KencelenganModel>
    suspend fun getAllKencelengan(): Flow<ResourceState<List<KencelenganModel>>>
    fun deleteKenclengan(kencelId: String): Flow<ResourceState<Boolean>>
    suspend fun scheduleAllKencelItemNotifications()
}