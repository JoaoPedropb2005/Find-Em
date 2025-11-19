package com.example.findem.model

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


    private fun getMockPets() =
        listOf(
            Pet(1, "Ludovico", "Pelo curto BR", "Rua ***", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "perdidos", "Minha rua"),
            Pet(2, "Sarapatel", "Europeu", "Rua ***", "****",
                android.R.drawable.ic_menu_gallery, "cachorro", "adocao", "Minha rua"),
            Pet(3, "Snowbell", "Persa", "Rua ***", "****",
                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Minha rua"),
            Pet(4, "Luciano", "Doméstico", "Rua ***", "****",
                android.R.drawable.ic_menu_gallery, "ave", "adocao", "Minha rua"),
            Pet(5, "Leãonardo", "Curto", "Rua ***", "****",
                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Minha rua"),
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

