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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Map // Nova importação para o ícone do Mapa


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindEmScreen(viewModel: FindEmViewModel,
                 onMapClick: () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    // ATUALIZAÇÃO: Adicionando a aba MAPA
    val tabItems = listOf(
        Pair("PERDIDOS", Icons.Default.Search),
        Pair("ADOÇÃO", Icons.Default.Favorite),
        Pair("ENCONTRADOS", Icons.Default.Done),
        Pair("MAPA", Icons.Filled.Map) // Novo item para o Mapa
    )

    // O índice da aba Mapa é o último (3)
    val MAP_TAB_INDEX = tabItems.size - 1

    val petsFiltrados = viewModel.getListaFiltrada()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PetDialog(
            onDismiss = { showDialog = false },
            onConfirm = { novoPet ->
                viewModel.addPetComGeocoding(context, novoPet)

                // Lógica para selecionar a aba após a postagem
                viewModel.selectedTab.value = when (novoPet.categoria.lowercase()) {
                    "perdidos" -> 0
                    "adocao", "adoção" -> 1
                    "encontrados" -> 2
                    else -> 0
                }

                // Reseta os filtros de espécie
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

                    // O ícone de mapa na TopBar foi removido, pois agora está na BottomBar
                    actions = {
                        /* IconButton(onClick = onMapClick) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Mapa",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        */
                        Spacer(modifier = Modifier.size(56.dp))
                    },

                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
                )
            },

            // BARRA INFERIOR (BOTTOM BAR)
            bottomBar = {
                TabRow(
                    selectedTabIndex = if (viewModel.selectedTab.value == MAP_TAB_INDEX) -1 else viewModel.selectedTab.value, // Desseleciona se estiver no mapa
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    tabItems.forEachIndexed { i, tabItem ->
                        Tab(
                            selected = viewModel.selectedTab.value == i,
                            onClick = {
                                if (i == MAP_TAB_INDEX) {
                                    // Se clicar no Mapa, chama a ação e NÃO muda a aba do VM
                                    onMapClick()
                                } else {
                                    // Se clicar em PERDIDOS/ADOÇÃO/ENCONTRADOS, muda a aba
                                    viewModel.selectedTab.value = i
                                }
                            },
                            icon = { Icon(tabItem.second, contentDescription = tabItem.first) },
                            text = { Text(tabItem.first) }
                        )
                    }
                }
            },

            floatingActionButton = {
                // Mantemos o FAB, que é independente da navegação principal da BottomBar
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        ) { padding ->

            // CONTEÚDO PRINCIPAL: SÓ MOSTRA SE NÃO ESTIVER NA ABA MAPA
            if (viewModel.selectedTab.value != MAP_TAB_INDEX) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF5F5F5))
                ) {

                    // começo do bloco de filtros
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
                    // final do bloco de filtros

                    // começo do bloco de LazyVerticalGrid (Lista de Cards)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = petsFiltrados) { pet ->
                            PetCard(pet = pet)
                        }
                    }
                    // final do bloco de LazyVerticalGrid (Lista de Cards)
                }
            } else {
                // IMPORTANTE: Adiciona um Spacer para preencher o espaço, mas o mapa
                // é gerenciado pela navegação principal (onMapClick)
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
// ... (O restante das funções PetCard, FilterChip, etc. permanece inalterado)
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