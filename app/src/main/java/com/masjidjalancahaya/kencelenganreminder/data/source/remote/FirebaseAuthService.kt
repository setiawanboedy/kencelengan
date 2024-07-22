package com.masjidjalancahaya.kencelenganreminder.data.source.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun authWithGoogle(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    fun isAuthLogin() = firebaseAuth.currentUser != null

    fun signOut(): Boolean{
        firebaseAuth.signOut()
        return firebaseAuth.currentUser == null
    }
}