package com.masjidjalancahaya.kencelenganreminder.data.repository

import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.data.model.KencelenganModel
import kotlinx.coroutines.flow.Flow


interface Repository {
    fun signWithGoogle(idToken: String, callback: (Boolean) -> Unit)
    fun isAuthLoginGoogle(): Boolean
    fun signOut(callback: (Boolean) -> Unit)

    suspend fun createKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>>
    suspend fun updateKencelengan(kencelenganModel: KencelenganModel): Flow<ResourceState<Boolean>>
    suspend fun getKencelById(kencelId: String): Flow<KencelenganModel>
    suspend fun getAllKencelengan(): Flow<ResourceState<List<KencelenganModel>>>
    fun deleteKenclengan(kencelId: String): Flow<ResourceState<Boolean>>
    suspend fun scheduleAllKencelItemNotifications()
}