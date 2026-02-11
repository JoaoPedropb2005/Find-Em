package com.example.findem

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.findem.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import android.location.Location
import com.google.android.gms.location.*

class FindEmService : Service() {
    private val db = FirebaseFirestore.getInstance()
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        iniciarEscutaFirestore()
        iniciarRastreioLocalizacao()
    }

    private fun iniciarRastreioLocalizacao() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
        try {
            fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    lastLocation = result.lastLocation
                }
            }, mainLooper)
        } catch (e: SecurityException) { }
    }

    private fun iniciarEscutaFirestore() {
        // Escuta o banco mesmo com o app fechado
        db.collection("pets").addSnapshotListener { value, _ ->
            val pets = value?.toObjects<Pet>() ?: return@addSnapshotListener
            val petRecente = pets.maxByOrNull { it.dataCriacao } ?: return@addSnapshotListener

            val meuId = FirebaseAuth.getInstance().currentUser?.uid
            if (petRecente.userId != meuId && lastLocation != null) {
                val dist = FloatArray(1)
                Location.distanceBetween(
                    lastLocation!!.latitude, lastLocation!!.longitude,
                    petRecente.latitude, petRecente.longitude, dist
                )
                if (dist[0] <= 2000) {
                    enviarNotificacao(petRecente)
                }
            }
        }
    }

    private fun enviarNotificacao(pet: Pet) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "PET_ALERT")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setContentTitle("Pet por perto!")
            .setContentText("${pet.nome} postado a menos de 2km.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(pet.id.hashCode(), builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null
}