package com.example.findem.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
fun MyPostScreen(viewModel: FindEmViewModel, onBack: () -> Unit, onPetClick: () -> Unit) {
    val currentUserId = viewModel.currentUser?.uid

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val compactMode = configuration.screenWidthDp.dp < 360.dp

    val minhasPostagens = if (currentUserId != null) {
        viewModel.pets.filter { it.userId == currentUserId }
    } else {
        emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Postagens", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        if (minhasPostagens.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Você ainda não possui postagens.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(minhasPostagens) { pet ->
                    Box {
                        PetCard(
                            pet = pet,
                            compact = compactMode,
                            onClick = {
                                viewModel.petSelecionadoParaDetalhes = pet
                                onPetClick()
                            }
                        )

                        IconButton(
                            onClick = { viewModel.deletarPet(pet) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, "Apagar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}