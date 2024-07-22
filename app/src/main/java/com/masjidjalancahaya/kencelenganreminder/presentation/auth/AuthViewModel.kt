package com.masjidjalancahaya.kencelenganreminder.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.masjidjalancahaya.kencelenganreminder.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkLoginSession()
    }

    fun loginWithGoogle(idToken: String){
        repository.signWithGoogle(idToken){ success ->
            if (success){
                _authState.value = AuthState.Authenticated
            }else{
                _authState.value = AuthState.Error("Auth Failed.")
            }
        }
    }

    fun logout(){
        repository.signOut(){ isSignOut ->
            if (isSignOut){
                _authState.value = AuthState.Unauthenticated
            }else{
                _authState.value = AuthState.Error("Logout Failed")
            }
        }
    }

    private fun checkLoginSession(){
        if (repository.isAuthLoginGoogle()){
            _authState.value = AuthState.Authenticated
        }else{
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState{
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}