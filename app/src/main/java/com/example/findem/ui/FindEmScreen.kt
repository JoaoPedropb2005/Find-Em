package com.example.findem.ui.theme

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.findem.LoginActivity
import com.example.findem.model.FindEmViewModel
import com.example.findem.model.Pet
import com.example.findem.ui.FindEmDrawerContent
import com.example.findem.ui.PetDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindEmScreen(
    viewModel: FindEmViewModel,
    navController: NavController,
    onMapClick: () -> Unit,
    onPetClick: (Pet) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val tabItems = listOf(
        Pair("PERDIDOS", Icons.Default.Search),
        Pair("ADOÇÃO", Icons.Default.Favorite),
        Pair("ENCONTRADOS", Icons.Default.Done),
        Pair("MAPA", Icons.Default.Map)
    )

    val MAP_TAB_INDEX = tabItems.size - 1
    val petsFiltrados = viewModel.getListaFiltrada()
    var showDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FindEmDrawerContent(
                pets = viewModel.pets,
                userName = viewModel.userName,
                isUserLoggedIn = viewModel.currentUser != null,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLoginClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                onLogoutClick = {
                    viewModel.logout()
                    scope.launch { drawerState.close() }
                },
                onMyPostsClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("minhas_postagens")
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
                    actions = { Spacer(modifier = Modifier.size(56.dp)) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
                )
            },
            bottomBar = {
                TabRow(
                    selectedTabIndex = if (viewModel.selectedTab.value == MAP_TAB_INDEX) -1 else viewModel.selectedTab.value,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    tabItems.forEachIndexed { i, tabItem ->
                        Tab(
                            selected = viewModel.selectedTab.value == i,
                            onClick = {
                                if (i == MAP_TAB_INDEX) onMapClick()
                                else viewModel.selectedTab.value = i
                            },
                            icon = { Icon(tabItem.second, contentDescription = tabItem.first) },
                            text = { Text(tabItem.first) }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (viewModel.currentUser != null) {
                            showDialog = true
                        } else {
                            Toast.makeText(context, "Você precisa estar logado para postar.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        ) { padding ->
            if (viewModel.selectedTab.value != MAP_TAB_INDEX) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF5F5F5))
                ) {
                    // Bloco de Filtros
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PetFilterChip(viewModel.filtroCachorros.value, "Cachorros") {
                            viewModel.filtroCachorros.value = !viewModel.filtroCachorros.value
                        }
                        PetFilterChip(viewModel.filtroGatos.value, "Gatos") {
                            viewModel.filtroGatos.value = !viewModel.filtroGatos.value
                        }
                        PetFilterChip(viewModel.filtroAves.value, "Aves") {
                            viewModel.filtroAves.value = !viewModel.filtroAves.value
                        }
                        PetFilterChip(viewModel.filtroOutros.value, "Outros") {
                            viewModel.filtroOutros.value = !viewModel.filtroOutros.value
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = petsFiltrados) { pet ->
                            PetCard(pet = pet, onClick = { onPetClick(pet) })
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }

    if (showDialog) {
        PetDialog(
            onDismiss = { showDialog = false },
            onConfirm = { petTemporario ->
                val uriImagem = if (petTemporario.imageUrl.isNotBlank())
                    Uri.parse(petTemporario.imageUrl) else null

                viewModel.salvarPetComFoto(uriImagem, petTemporario)

                viewModel.selectedTab.value = when (petTemporario.categoria.lowercase()) {
                    "perdidos" -> 0
                    "adocao", "adoção" -> 1
                    "encontrados" -> 2
                    else -> 0
                }
                showDialog = false
            },
            viewModel = viewModel
        )
    }
}
@Composable
fun PetCard(pet: Pet, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        if (pet.imageUrl.isNotBlank()) {
            AsyncImage(
                model = pet.imageUrl,
                contentDescription = pet.nome,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_camera),
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )
        } else {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = pet.nome,
                modifier = Modifier.height(120.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(pet.nome, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        Text(pet.raca, fontSize = 12.sp, color = Color.DarkGray)
        Text(pet.endereco, fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
    }
}

@Composable
fun PetFilterChip(selected: Boolean, text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text, fontSize = 12.sp) },
        leadingIcon = {
            if (selected) Icon(Icons.Default.Check, contentDescription = null)
        }
    )
}