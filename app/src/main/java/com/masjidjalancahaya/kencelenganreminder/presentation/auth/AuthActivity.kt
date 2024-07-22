package com.masjidjalancahaya.kencelenganreminder.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityAuthBinding
import com.masjidjalancahaya.kencelenganreminder.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityAuthBinding

    private lateinit var googleSignClient: GoogleSignInClient

    private val googleSignLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null){
                authViewModel.loginWithGoogle(account.idToken!!)
            }
        }catch (e: ApiException){
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1027399262279-66de1i4v2keag8qq56pvpu7u8sbf4s49.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignClient = GoogleSignIn.getClient(this, gso)

        observeAuth()

        binding.login.setOnClickListener {
            val signIntent = googleSignClient.signInIntent
            googleSignLauncher.launch(signIntent)
        }
    }

    private fun observeAuth(){
        authViewModel.authState.observe(this){ auth ->
            when(auth){
                is AuthState.Authenticated -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthState.Unauthenticated -> {
                    googleSignClient.signOut()
                }
                is AuthState.Error -> {
                    Toast.makeText(this, auth.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}