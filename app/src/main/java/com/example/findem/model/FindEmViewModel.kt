package com.example.findem.model

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FindEmViewModel : ViewModel() {

    // --- VARIÁVEIS DE ESTADO DA APLICAÇÃO ---
    private val _pets = getMockPets().toMutableStateList()

    val pets: List<Pet>
        get() = _pets.toList()

    var selectedTab = mutableStateOf(0)
    var filtroCachorros = mutableStateOf(false)
    var filtroGatos =  mutableStateOf(false)
    var filtroAves =  mutableStateOf(false)
    var filtroOutros =  mutableStateOf(false)


    var mapFiltroCachorros = mutableStateOf(false)
    var mapFiltroGatos = mutableStateOf(false)
    var mapFiltroAves = mutableStateOf(false)
    var mapFiltroOutros = mutableStateOf(false)

    val notificacoes = listOf(
        Notificacao(1, "Cachorro perdido a 1km", "1km"),
        Notificacao(2, "Gato perdido a 2km", "2km"),
        Notificacao(3, "Ave perdida a 3km", "3km")
    )

    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    var userName by mutableStateOf<String?>("Visitante")
        private set

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        currentUser = user
        userName = user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@")
                    ?: "Visitante"
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
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
        fetchEstados()
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

    // --- FUNÇÃO DE GEOCODING (CORRIGIDA) ---

    fun addPetComGeocoding(context: Context, novoPetSemCoordenadas: Pet) {

        val enderecoCompleto = novoPetSemCoordenadas.endereco // Usando o endereço formatado

        if (enderecoCompleto.isBlank()) {
            salvarPetFinal(novoPetSemCoordenadas, 0.0, 0.0)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Novo método assíncrono para Tiramisu+
                    geocoder.getFromLocationName(enderecoCompleto, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val lat = addresses[0].latitude
                            val lon = addresses[0].longitude
                            salvarPetFinal(novoPetSemCoordenadas, lat, lon)
                        } else {
                            salvarPetFinal(novoPetSemCoordenadas, 0.0, 0.0)
                        }
                    }
                } else {
                    // Método síncrono para versões antigas
                    val addresses = geocoder.getFromLocationName(enderecoCompleto, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val lat = addresses[0].latitude
                        val lon = addresses[0].longitude
                        salvarPetFinal(novoPetSemCoordenadas, lat, lon)
                    } else {
                        salvarPetFinal(novoPetSemCoordenadas, 0.0, 0.0)
                    }
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Erro de Geocoding para $enderecoCompleto: ${e.message}")
                salvarPetFinal(novoPetSemCoordenadas, 0.0, 0.0)
            }
        }
    }

    private fun salvarPetFinal(pet: Pet, lat: Double, lon: Double) {
        val petCompleto = pet.copy(
            latitude = lat,
            longitude = lon
        )

        viewModelScope.launch(Dispatchers.Main) {
            _pets.add(petCompleto)
        }
    }

    // ... (getMockPets, addPet, removePet, getListaFiltrada continuam os mesmos)
    private fun getMockPets() =
        listOf(
            Pet(1, "Ludovico", "Pelo curto BR", "Rua A, PE", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "perdidos", "Boa Viagem", -8.1111, -34.8910),
            Pet(2, "Sarapatel", "Europeu", "Rua B, PE", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "adocao", "Pina", -8.0930, -34.8800),
            Pet(3, "Snowbell", "Persa", "Rua C, PE", "****",
                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Derby", -8.0570, -34.9000),
            Pet(4, "Luciano", "Doméstico", "Rua D, CE", "****",
                android.R.drawable.ic_menu_gallery, "ave", "adocao", "Recife Antigo", -8.0630, -34.8710),
            Pet(5, "Leãonardo", "Curto", "Rua E, BA", "****",
                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Olinda", -8.0090, -34.8550),
            Pet(6, "Diana", "Bombaim", "Rua ***", "****",
                android.R.drawable.ic_menu_gallery, "outro", "perdidos", "Minha rua"),
            Pet(7, "Thor", "SRD", "Rua X", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "encontrados", "Minha rua")
        )

    fun addPet(pet: Pet){
        _pets.add(pet)
    }

    fun removePet(pet: Pet){
        _pets.remove(pet)
    }

    fun getListaFiltrada(): List<Pet>{
        return _pets.filter { pet ->
            val categoriaOk = when (selectedTab.value){
                0 -> pet.categoria == "perdidos"
                1 -> pet.categoria == "adocao"
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