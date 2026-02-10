package com.example.findem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.findem.model.FindEmViewModel
import com.example.findem.ui.theme.PetCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FindEmViewModel,
    onBack: () -> Unit,
    onPetClick: () -> Unit
) {
    // Busca a lista filtrada de favoritos do ViewModel
    val listaFavoritos = viewModel.petsFavoritos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (listaFavoritos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text("Você ainda não favoritou nenhum pet.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaFavoritos) { pet ->
                    PetCard(
                        pet = pet,
                        isFavorite = true,
                        onFavoriteClick = {
                            viewModel.toggleFavorito(pet)
                        },
                        onClick = {
                            // Salva e navega para detalhes
                            viewModel.petSelecionadoParaDetalhes = pet
                            onPetClick()
                        }
                    )
                }
            }
        }
    }
}