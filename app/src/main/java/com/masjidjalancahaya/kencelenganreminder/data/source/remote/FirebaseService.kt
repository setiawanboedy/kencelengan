package com.masjidjalancahaya.kencelenganreminder.data.source.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.masjidjalancahaya.kencelenganreminder.data.model.KencelenganModel
import timber.log.Timber
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val store: FirebaseFirestore
){
    companion object{
        const val KENCELENGAN = "kencelengans"
    }

    fun getAllKencelengan(): Task<QuerySnapshot> {
        val kencelengan = store.collection(KENCELENGAN)

        return kencelengan.get()
    }

    fun getKencelById(kencelId: String): Task<DocumentSnapshot>{
        val kencelengan = store.collection(KENCELENGAN)
        return kencelengan.document(kencelId).get()
    }

    fun createKencelengan(kencelenganModel: KencelenganModel): Task<DocumentReference>{
        val kencelengan = store.collection(KENCELENGAN)

        return kencelengan.add(kencelenganModel.toMap())
    }

    fun updateKencelengan(idDoc: String, kencelenganModel: KencelenganModel): Task<Void> {
        val kencelengan = store.collection(KENCELENGAN)
        val update = kencelenganModel.copy(id = idDoc)
        return kencelengan.document(idDoc).update(update.toMap())
    }

    fun deleteKencelengan(idDoc: String): Task<Void>{
        val kencelengan = store.collection(KENCELENGAN)
        return kencelengan.document(idDoc).delete()
    }
}