package com.example.findem.model

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class FindEmViewModel(application: Application) : AndroidViewModel(application) {

    // --- ESTADO DOS PETS ---
    private val db = FirebaseFirestore.getInstance()
    private val _pets = mutableStateListOf<Pet>()
    val pets: List<Pet> get() = _pets
    val favoritosIds = mutableStateListOf<String>()

    val petsFavoritos: List<Pet> get() = _pets.filter { favoritosIds.contains(it.id) }

    // --- ESTADO DE LOCALIZAÇÃO E FILTROS ---
    var userLocation by mutableStateOf<LatLng?>(null)
    var selectedTab = mutableStateOf(0)
    var filtroCachorros = mutableStateOf(false)
    var filtroGatos = mutableStateOf(false)
    var filtroAves = mutableStateOf(false)
    var filtroOutros = mutableStateOf(false)

    // --- ESTADOS DO USUÁRIO ---
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set
    var userName by mutableStateOf<String?>("Visitante")
        private set
    var userWhatsapp by mutableStateOf("")

    val petsFiltradosMap by derivedStateOf {
        val semFiltro = !filtroCachorros.value && !filtroGatos.value && !filtroAves.value && !filtroOutros.value

        if (semFiltro) {
            _pets // Retorna tudo se nada estiver marcado
        } else {
            _pets.filter { pet ->
                (filtroCachorros.value && pet.especie == "cachorro") ||
                        (filtroGatos.value && pet.especie == "gato") ||
                        (filtroAves.value && pet.especie == "ave") ||
                        (filtroOutros.value && pet.especie == "outro")
            }
        }
    }

    var petSelecionadoParaDetalhes by mutableStateOf<Pet?>(null)

    // --- IBGE ---
    val estadosIBGE = mutableStateOf<List<Estado>>(emptyList())
    val municipiosIBGE = mutableStateOf<List<Municipio>>(emptyList())
    private val ibgeService: IBGEService = RetrofitClient.ibgeService

    init {
        configurarAuth()
        fetchEstados()
        fetchPetsDoFirestore()
    }

    // --- LÓGICA DE LOCALIZAÇÃO (CORRIGE O ERRO 'updateUserLocation') ---
    fun updateUserLocation(lat: Double, lng: Double) {
        userLocation = LatLng(lat, lng)
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    // --- NOTIFICAÇÕES (CORRIGE O ERRO 'notificacoesProximas') ---
    val notificacoesProximas by derivedStateOf {
        val localUsuario = userLocation ?: return@derivedStateOf emptyList<Notificacao>()

        petsFiltradosMap.mapNotNull { pet ->
            if (pet.latitude == 0.0 || pet.longitude == 0.0) return@mapNotNull null

            val distanciaMetros = calcularDistancia(
                localUsuario.latitude, localUsuario.longitude,
                pet.latitude, pet.longitude
            )

            if (distanciaMetros <= 1000) {
                val distanciaTexto = if (distanciaMetros < 1000) {
                    "${distanciaMetros.toInt()}m"
                } else {
                    "%.1f km".format(distanciaMetros / 1000f)
                }

                Notificacao(
                    id = pet.id.hashCode(),
                    petId = pet.id,
                    mensagem = "${pet.especie.capitalize()} ${pet.categoria} a $distanciaTexto",
                )
            } else null
        }.sortedBy { it.id }
    }

    fun getPetById(id: String): Pet? {
        return _pets.find { it.id == id }
    }

    // --- RESTANTE DA LÓGICA (AUTH, FIRESTORE, CLOUDINARY) ---

    private fun configurarAuth() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            currentUser = user
            if (user != null) {
                userName = user.displayName ?: user.email?.substringBefore("@") ?: "Usuário"

                db.collection("usuarios").document(user.uid).addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        userWhatsapp = snapshot.getString("whatsapp") ?: ""
                        snapshot.getString("nome")?.let { if (it.isNotBlank()) userName = it }

                        val favs = snapshot.get("favoritos") as? List<String> ?: emptyList()
                        favoritosIds.clear()
                        favoritosIds.addAll(favs)
                    }
                }
            } else {
                userName = "Visitante"
                userWhatsapp = ""
                favoritosIds.clear()
            }
        }
    }

    private fun fetchPetsDoFirestore() {
        db.collection("pets")
            .orderBy("dataCriacao", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    _pets.clear()
                    _pets.addAll(value.toObjects<Pet>())
                }
            }
    }

    fun salvarPetComFoto(uriImagem: Uri?, novoPet: Pet) {
        if (novoPet.imageUrl.startsWith("http") && uriImagem == null) {
            addPetComGeocodingESalvar(novoPet)
            return
        }
        if (uriImagem != null) {
            MediaManager.get().upload(uriImagem)
                .unsigned("findem_preset")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, b: Long, t: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as String
                        addPetComGeocodingESalvar(novoPet.copy(imageUrl = url))
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        addPetComGeocodingESalvar(novoPet)
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                }).dispatch()
        } else addPetComGeocodingESalvar(novoPet)
    }

    private fun addPetComGeocodingESalvar(pet: Pet) {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocationName(pet.endereco, 1)
                val lat = addresses?.firstOrNull()?.latitude ?: 0.0
                val lon = addresses?.firstOrNull()?.longitude ?: 0.0
                salvarNoFirestore(pet, lat, lon)
            } catch (e: Exception) {
                salvarNoFirestore(pet, 0.0, 0.0)
            }
        }
    }

    private fun salvarNoFirestore(pet: Pet, lat: Double, lon: Double) {
        val docId = if (pet.id.isBlank()) db.collection("pets").document().id else pet.id
        val petFinal = pet.copy(
            id = docId,
            latitude = lat,
            longitude = lon,
            userId = currentUser?.uid ?: "",
            ownerContato = userWhatsapp,
            dataCriacao = if (pet.dataCriacao == 0L) System.currentTimeMillis() else pet.dataCriacao
        )
        db.collection("pets").document(docId).set(petFinal)
    }

    fun marcarComoEncontrado(pet: Pet, onSuccess: () -> Unit) {
        if (pet.id.isNotBlank()) {
            db.collection("pets").document(pet.id)
                .update("categoria", "encontrados")
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    Log.e("FindEm", "Erro ao atualizar: ${it.message}")
                }
        }
    }

    fun toggleFavorito(pet: Pet) {
        val uid = currentUser?.uid ?: return
        val userRef = db.collection("usuarios").document(uid)

        if (favoritosIds.contains(pet.id)) {
            userRef.update("favoritos", FieldValue.arrayRemove(pet.id))
            favoritosIds.remove(pet.id)
        } else {
            userRef.update("favoritos", FieldValue.arrayUnion(pet.id))
            favoritosIds.add(pet.id)
        }
    }

    fun getListaFiltrada(): List<Pet> {
        return _pets.filter { pet ->
            val categoriaOk = when (selectedTab.value) {
                0 -> pet.categoria == "perdidos"
                1 -> pet.categoria == "adocao" || pet.categoria == "adoção"
                else -> pet.categoria == "encontrados"
            }
            if (!categoriaOk) return@filter false
            val semFiltro = !filtroCachorros.value && !filtroGatos.value && !filtroAves.value && !filtroOutros.value
            if (semFiltro) return@filter true
            (filtroCachorros.value && pet.especie == "cachorro") ||
                    (filtroGatos.value && pet.especie == "gato") ||
                    (filtroAves.value && pet.especie == "ave") ||
                    (filtroOutros.value && pet.especie == "outro")
        }
    }

    fun deletarPet(pet: Pet) {
        if (pet.id.isNotBlank()) db.collection("pets").document(pet.id).delete()
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        userWhatsapp = ""
        userName = "Visitante"
    }

    fun fetchEstados() {
        viewModelScope.launch(Dispatchers.IO) {
            try { estadosIBGE.value = ibgeService.getEstados() } catch (e: Exception) {}
        }
    }

    fun fetchMunicipios(ufSigla: String) {
        municipiosIBGE.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            try { municipiosIBGE.value = ibgeService.getMunicipiosPorEstado(ufSigla) } catch (e: Exception) {}
        }
    }
}