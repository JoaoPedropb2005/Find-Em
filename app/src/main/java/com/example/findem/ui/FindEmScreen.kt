package com.example.findem.ui.theme



import android.content.Intent

import android.net.Uri

import android.widget.Toast

import androidx.compose.foundation.background

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.foundation.lazy.grid.items

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextOverflow

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



// Configuração para modo compacto (telas menores)

    val configuration = LocalConfiguration.current

    val compactMode = configuration.screenWidthDp.dp < 360.dp



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

                },

                onFavoriteClick = {

                    scope.launch { drawerState.close() }

                    if (viewModel.currentUser != null) {

                        navController.navigate("favorites")

                    } else {

                        Toast.makeText(context, "Logue para ver favoritos.", Toast.LENGTH_SHORT).show()

                    }

                },

                onSettingsClick = {

                    scope.launch { drawerState.close() }

                    navController.navigate("configuracoes")

                }

            )

        }

    ) {

        Scaffold(

            topBar = {

                TopAppBar(

                    navigationIcon = {

                        IconButton(onClick = { scope.launch { drawerState.open() } }) {

                            Icon(Icons.Default.Menu, null, tint = Color.Black, modifier = Modifier.size(32.dp))

                        }

                    },

                    title = {

                        Text("FIND'EM", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)

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

                            icon = { Icon(tabItem.second, null) },

                            text = { Text(tabItem.first, fontSize = 8.sp, maxLines = 1, softWrap = false) }

                        )

                    }

                }

            },

            floatingActionButton = {

                FloatingActionButton(

                    onClick = {

                        if (viewModel.currentUser != null) showDialog = true

                        else Toast.makeText(context, "Logue para postar.", Toast.LENGTH_SHORT).show()

                    },

                    containerColor = Color(0xFF4CAF50)

                ) {

                    Icon(Icons.Default.Add, null, tint = Color.White)

                }

            }

        ) { padding ->

            if (viewModel.selectedTab.value != MAP_TAB_INDEX) {

                Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {

                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {

                        PetFilterChip(viewModel.filtroCachorros.value, "Cães", compactMode) { viewModel.filtroCachorros.value = !viewModel.filtroCachorros.value }

                        PetFilterChip(viewModel.filtroGatos.value, "Gatos", compactMode) { viewModel.filtroGatos.value = !viewModel.filtroGatos.value }

                        PetFilterChip(viewModel.filtroAves.value, "Aves", compactMode) { viewModel.filtroAves.value = !viewModel.filtroAves.value }

                        PetFilterChip(viewModel.filtroOutros.value, "Outros", compactMode) { viewModel.filtroOutros.value = !viewModel.filtroOutros.value }

                    }



                    LazyVerticalGrid(

                        columns = GridCells.Fixed(2),

                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),

                        horizontalArrangement = Arrangement.spacedBy(12.dp),

                        verticalArrangement = Arrangement.spacedBy(12.dp)

                    ) {

                        items(items = petsFiltrados) { pet ->

                            val isFavorite = viewModel.favoritosIds.contains(pet.id)

                            PetCard(

                                pet = pet,

                                isFavorite = isFavorite,

                                onFavoriteClick = {

                                    if (viewModel.currentUser != null) viewModel.toggleFavorito(pet)

                                    else Toast.makeText(context, "Logue para favoritar!", Toast.LENGTH_SHORT).show()

                                },

                                compact = compactMode,

                                onClick = { onPetClick(pet) }

                            )

                        }

                    }

                }

            }

        }

    }



    if (showDialog) {

        PetDialog(

            onDismiss = { showDialog = false },

            onConfirm = { petTemp ->

                val uri = if (petTemp.imageUrl.isNotBlank() && !petTemp.imageUrl.startsWith("http")) Uri.parse(petTemp.imageUrl) else null

                viewModel.salvarPetComFoto(uri, petTemp)

                showDialog = false

            },

            viewModel = viewModel

        )

    }

}

@Composable

fun PetCard(
    pet: Pet,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }

    ) {

        Column(modifier = Modifier.fillMaxWidth().padding(if(compact) 4.dp else 8.dp)) {

            AsyncImage(

                model = pet.imageUrl,

                contentDescription = null,

                modifier = Modifier

                    .height(if(compact) 100.dp else 120.dp)

                    .fillMaxWidth()

                    .clip(RoundedCornerShape(12.dp)),

                contentScale = ContentScale.Crop,

                placeholder = painterResource(id = android.R.drawable.ic_menu_camera)

            )

            Spacer(Modifier.height(6.dp))

            Text(pet.nome, fontSize = if(compact) 14.sp else 16.sp, color = Color.Black, fontWeight = FontWeight.Bold, maxLines = 1)

            Text(pet.endereco, fontSize = if(compact) 10.sp else 12.sp, color = Color.DarkGray, maxLines = 1, overflow = TextOverflow.Ellipsis)

        }



        if (onFavoriteClick != null) {

            IconButton(

                onClick = onFavoriteClick,

                modifier = Modifier

                    .align(Alignment.TopEnd)

                    .padding(4.dp)

                    .background(Color.White.copy(alpha = 0.7f), CircleShape)

                    .size(if(compact) 28.dp else 32.dp)

            ) {

                Icon(

                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,

                    contentDescription = null,

                    tint = if (isFavorite) Color.Red else Color.Gray,

                    modifier = Modifier.size(if(compact) 18.dp else 20.dp)

                )

            }

        }

    }

}



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun PetFilterChip(selected: Boolean, text: String, compact: Boolean, onClick: () -> Unit) {

    FilterChip(

        selected = selected,

        onClick = onClick,

        label = { Text(text, fontSize = if(compact) 10.sp else 12.sp) },

        leadingIcon = if (selected) {

            { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }

        } else null

    )

}