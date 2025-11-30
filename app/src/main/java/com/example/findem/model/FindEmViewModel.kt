package com.example.findem.model

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.findem.model.Pet

class FindEmViewModel : ViewModel() {

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

    fun addPetComGeocoding(context: Context, novoPetSemCoordenadas: Pet) {

        viewModelScope.launch(Dispatchers.IO) { // Roda fora da tela principal (IO)

            var lat = 0.0
            var lon = 0.0

            if (novoPetSemCoordenadas.descricaoLocal.isNotBlank()) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocationName(novoPetSemCoordenadas.descricaoLocal, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                lat = addresses[0].latitude
                                lon = addresses[0].longitude
                                salvarPetFinal(novoPetSemCoordenadas, lat, lon)
                            } else {
                                salvarPetFinal(novoPetSemCoordenadas, 0.0, 0.0)
                            }
                        }
                    } else {
                        val addresses = geocoder.getFromLocationName(novoPetSemCoordenadas.descricaoLocal, 1)
                        if (!addresses.isNullOrEmpty()) {
                            lat = addresses[0].latitude
                            lon = addresses[0].longitude
                        }
                        salvarPetFinal(novoPetSemCoordenadas, lat, lon)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    salvarPetFinal(novoPetSemCoordenadas, 0.0, 0.0)
                }
            } else {
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

    private fun getMockPets() =
        listOf(
            Pet(1, "Ludovico", "Pelo curto BR", "Rua A", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "perdidos", "Boa Viagem", -8.1111, -34.8910),
            Pet(2, "Sarapatel", "Europeu", "Rua B", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "adocao", "Pina", -8.0930, -34.8800),
            Pet(3, "Snowbell", "Persa", "Rua C", "****",
                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Derby", -8.0570, -34.9000),
            Pet(4, "Luciano", "Doméstico", "Rua D", "****",
                android.R.drawable.ic_menu_gallery, "ave", "adocao", "Recife Antigo", -8.0630, -34.8710),
            Pet(5, "Leãonardo", "Curto", "Rua E", "****",
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

