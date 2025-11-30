package com.example.findem.ui.theme

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.model.FindEmViewModel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.findem.LoginActivity
import com.example.findem.model.Pet
import com.example.findem.ui.FindEmDrawerContent
import com.example.findem.ui.PetDialog
import kotlinx.coroutines.launch

//private val Any.value: Int
//private val FindEmViewModel.selectedTab: Any

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindEmScreen(viewModel: FindEmViewModel,
                 onMapClick: () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val tabs = listOf("PERDIDOS", "ADOÇÃO", "ENCONTRADOS")

    val petsFiltrados = viewModel.getListaFiltrada()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PetDialog(
            onDismiss = { showDialog = false },
            onConfirm = { novoPet ->
                //viewModel.addPet(novoPet)
                viewModel.addPetComGeocoding(context, novoPet)
                viewModel.selectedTab.value = when (novoPet.categoria.lowercase()) {
                    "perdidos" -> 0
                    "adocao", "adoção" -> 1
                    "encontrados" -> 2
                    else -> 0
                }

                viewModel.filtroCachorros.value = false
                viewModel.filtroGatos.value = false
                viewModel.filtroAves.value = false
                viewModel.filtroOutros.value = false

                showDialog = false
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FindEmDrawerContent(
                pets = viewModel.pets,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },
                onLoginClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            )
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },

                    title = {
                        Text(
                            "FIND'EM",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },

                    actions = {
                        IconButton(onClick = onMapClick) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Mapa",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
                )
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5))
            ) {

                TabRow(selectedTabIndex = viewModel.selectedTab.value) {
                    tabs.forEachIndexed { i, t ->
                        Tab(
                            selected = viewModel.selectedTab.value == i,
                            onClick = { viewModel.selectedTab.value = i },
                            text = { Text(t) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(viewModel.filtroCachorros.value, "Cachorros") {
                        viewModel.filtroCachorros.value = !viewModel.filtroCachorros.value
                    }
                    FilterChip(viewModel.filtroGatos.value, "Gatos") {
                        viewModel.filtroGatos.value = !viewModel.filtroGatos.value
                    }
                    FilterChip(viewModel.filtroAves.value, "Aves") {
                        viewModel.filtroAves.value = !viewModel.filtroAves.value
                    }
                    FilterChip(viewModel.filtroOutros.value, "Outros") {
                        viewModel.filtroOutros.value = !viewModel.filtroOutros.value
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = petsFiltrados) { pet ->
                        PetCard(pet = pet)
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: com.example.findem.model.Pet) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(8.dp)
    ) {

        Image(
            painter = painterResource(id = pet.imagemRes),
            contentDescription = pet.nome,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(6.dp))

        Text(pet.nome, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        Text(pet.raca, fontSize = 12.sp, color = Color.DarkGray)
        Text(pet.endereco, fontSize = 12.sp, color = Color.DarkGray)
        Text(pet.descricaoLocal, fontSize = 12.sp, color = Color.DarkGray)
    }

}

@Composable
fun FilterChip(selected: Boolean, text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text, fontSize = 12.sp) },
        leadingIcon = {
            if (selected) Icon(Icons.Default.Check, contentDescription = null)
        }
    )
}
