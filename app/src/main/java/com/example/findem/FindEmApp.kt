package com.example.findem

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FindEmApp : Application() {
    val FLAGS = FLAG_ACTIVITY_SINGLE_TOP or
            FLAG_ACTIVITY_NEW_TASK or
            FLAG_ACTIVITY_CLEAR_TASK

    override fun onCreate() {
        super.onCreate()
        Firebase.auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                goToMain()
            } else {
                goToLogin()
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = FLAGS
            putExtra("destino_inicial", "home")
        }
        this.startActivity(intent)
    }

    private fun goToLogin() {
        this.startActivity(Intent(this, LoginActivity::class.java).setFlags(FLAGS))
    }
}