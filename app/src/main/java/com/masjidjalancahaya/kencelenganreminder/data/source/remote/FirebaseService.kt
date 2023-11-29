package com.masjidjalancahaya.kencelenganreminder.data.source.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
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

    fun createKencelengan(kencelenganModel: KencelenganModel): Task<DocumentReference>{
        val kencelengan = store.collection(KENCELENGAN)
        return kencelengan.add(kencelenganModel.toMap())
    }

    fun upateKencelengan(idDoc: String, kencelenganModel: KencelenganModel): Task<Void> {
        val kencelengan = store.collection(KENCELENGAN)
        val model = KencelenganModel(
            id = idDoc,
            name = kencelenganModel.name,
            nomor = kencelenganModel.nomor,
            address = kencelenganModel.address
        )
        Timber.tag("iddoct").d(model.toString())
        return kencelengan.document(idDoc).update(model.toMap())
    }
}