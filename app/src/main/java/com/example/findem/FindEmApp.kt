package com.example.findem

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.cloudinary.android.MediaManager

class FindEmApp : Application() {
    val FLAGS = FLAG_ACTIVITY_SINGLE_TOP or
            FLAG_ACTIVITY_NEW_TASK or
            FLAG_ACTIVITY_CLEAR_TASK

    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()
        config["cloud_name"] = "ddjnkgpxd"
        MediaManager.init(this, config)

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