package com.example.findem.model

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class FindEmViewModel(application: Application) : AndroidViewModel(application) {

    // --- VARIÁVEIS DE ESTADO DA APLICAÇÃO ---
    var userLocation by mutableStateOf<LatLng?>(null)
    private val db = FirebaseFirestore.getInstance()
    private val _pets = mutableStateListOf<Pet>()
    val pets: List<Pet> get() = _pets
    var selectedTab = mutableStateOf(0)
    var filtroCachorros = mutableStateOf(false)
    var filtroGatos =  mutableStateOf(false)
    var filtroAves =  mutableStateOf(false)
    var filtroOutros =  mutableStateOf(false)
    var mapFiltroCachorros = mutableStateOf(false)
    var mapFiltroGatos = mutableStateOf(false)
    var mapFiltroAves = mutableStateOf(false)
    var mapFiltroOutros = mutableStateOf(false)
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set
    var userName by mutableStateOf<String?>("Visitante")
        private set

    private fun configurarAuth() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            currentUser = user
            userName = user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore("@") ?: "Visitante"
        }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        userLocation = LatLng(lat, lng)
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    val notificacoesProximas by derivedStateOf {
        val localUsuario = userLocation ?: return@derivedStateOf emptyList()

        _pets.mapNotNull { pet ->

            if (pet.latitude == 0.0 || pet.longitude == 0.0) return@mapNotNull null

            val distanciaMetros = calcularDistancia(
                localUsuario.latitude, localUsuario.longitude,
                pet.latitude, pet.longitude
            )

            if (distanciaMetros <= 5000) {
                val distanciaKm = "%.1f km".format(distanciaMetros / 1000f)
                Notificacao(
                    id = pet.id.hashCode(),
                    mensagem = "${pet.especie.capitalize()} ${pet.categoria} próximo: ${pet.nome}",
                    distancia = distanciaKm
                )
            } else {
                null
            }
        }.sortedBy { it.distancia }
    }

    private fun fetchPetsDoFirestore() {
        db.collection("pets")
            .orderBy("dataCriacao", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore", "Erro ao buscar pets: ", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    val lista = value.toObjects<Pet>()
                    _pets.clear()
                    _pets.addAll(lista)
                }
            }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    // --- VARIÁVEIS PARA LOCALIDADES DO IBGE (AGORA USADAS) ---
    val estadosIBGE = mutableStateOf<List<Estado>>(emptyList())
    val municipiosIBGE = mutableStateOf<List<Municipio>>(emptyList())
    // Instância do serviço Retrofit para IBGE
    private val ibgeService: IBGEService = RetrofitClient.ibgeService

    init {
        // Inicia a busca pelos estados ao criar o ViewModel
        configurarAuth()
        fetchEstados()
        fetchPetsDoFirestore()
    }

    // --- FUNÇÕES IBGE (Busca de Estados e Municípios) ---

    fun fetchEstados() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                estadosIBGE.value = ibgeService.getEstados()
            } catch (e: Exception) {
                Log.e("IBGE_API", "Erro ao buscar estados: ${e.message}")
            }
        }
    }

    fun fetchMunicipios(ufSigla: String) {
        // Limpa a lista de municípios anterior enquanto busca
        municipiosIBGE.value = emptyList()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                municipiosIBGE.value = ibgeService.getMunicipiosPorEstado(ufSigla)
            } catch (e: Exception) {
                Log.e("IBGE_API", "Erro ao buscar municípios para $ufSigla: ${e.message}")
            }
        }
    }

    fun salvarPetComFoto(uriImagem: Uri?, novoPet: Pet) {
        if (uriImagem != null) {
            Log.d("Cloudinary", "Iniciando upload...")
            // Upload
            MediaManager.get().upload(uriImagem)
                .unsigned("findem_preset")
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val urlDaImagem = resultData["secure_url"] as String
                        Log.d("Cloudinary", "Sucesso: $urlDaImagem")

                        val petComUrl = novoPet.copy(imageUrl = urlDaImagem)
                        addPetComGeocodingESalvar(petComUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("Cloudinary", "Erro: ${error.description}")
                        addPetComGeocodingESalvar(novoPet)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        } else {
            addPetComGeocodingESalvar(novoPet)
        }
    }


    private fun addPetComGeocodingESalvar(pet: Pet) {
        val context = getApplication<Application>().applicationContext
        val enderecoCompleto = pet.endereco

        if (enderecoCompleto.isBlank()) {
            salvarNoFirestore(pet, 0.0, 0.0)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(enderecoCompleto, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            salvarNoFirestore(pet, addresses[0].latitude, addresses[0].longitude)
                        } else {
                            salvarNoFirestore(pet, 0.0, 0.0)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(enderecoCompleto, 1)
                    if (!addresses.isNullOrEmpty()) {
                        salvarNoFirestore(pet, addresses[0].latitude, addresses[0].longitude)
                    } else {
                        salvarNoFirestore(pet, 0.0, 0.0)
                    }
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Erro: ${e.message}")
                salvarNoFirestore(pet, 0.0, 0.0)
            }
        }
    }

    private fun salvarNoFirestore(pet: Pet, lat: Double, lon: Double) {
        val novoId = db.collection("pets").document().id
        val uidUsuario = currentUser?.uid ?: ""

        val petFinal = pet.copy(
            id = novoId,
            latitude = lat,
            longitude = lon,
            userId = uidUsuario,
            dataCriacao = System.currentTimeMillis()
        )

        db.collection("pets").document(novoId).set(petFinal)
            .addOnSuccessListener {
                Log.d("Firestore", "Pet salvo com sucesso! ID: $novoId")
                // Não precisa adicionar manual no _pets, o SnapshotListener faz isso
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao salvar: ${e.message}")
            }
    }

    fun getListaFiltrada(): List<Pet>{
        return _pets.filter { pet ->
            val categoriaOk = when (selectedTab.value){
                0 -> pet.categoria == "perdidos"
                1 -> pet.categoria == "adocao" || pet.categoria == "adoção"
                else -> pet.categoria == "encontrados"
            }
            if(!categoriaOk) return@filter false

            val nenhumFiltroSelecionado =
                !filtroCachorros.value && !filtroGatos.value && !filtroAves.value && !filtroOutros.value
            if (nenhumFiltroSelecionado) return@filter true

            (filtroCachorros.value && pet.especie == "cachorro") ||
                    (filtroGatos.value && pet.especie == "gato") ||
                    (filtroAves.value && pet.especie == "ave") ||
                    (filtroOutros.value && pet.especie == "outro")
        }
    }
}
