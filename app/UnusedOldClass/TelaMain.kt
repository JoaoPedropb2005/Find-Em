package com.example.findem

import android.R.attr.navigationIcon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//data class Pet(
//    val id: Int,
//    val nome: String,
//    val raca: String,
//    val endereco: String,
//    val classificacao: String,
//    val imagemRes: Int,
//    val especie: String,
//    val categoria: String,
//    val ultimolocal: String
//)
//
//
//class TelaMain : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FindEmTheme {
//                FindEmScreen()
//            }
//        }
//    }
//}
//
//@Composable
//fun FindEmTheme(content: @Composable () -> Unit) {
//    MaterialTheme(
//        colorScheme = lightColorScheme(
//            primary = Color(0xFF4CAF50),
//            onPrimary = Color.White,
//            background = Color(0xFFF5F5F5),
//            onBackground = Color.Black
//        ),
//        content = content
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FindEmScreen() {
//
//    var abrirFormulario by remember { mutableStateOf(false) }
//    var selectedTab by remember { mutableStateOf(0) }
//    val tabs = listOf("PERDIDOS", "ADOÇÃO", "ENCONTRADOS")
//
//    // Filtros
//    var filtroCachorros by remember { mutableStateOf(false) }
//    var filtroGatos by remember { mutableStateOf(false) }
//    var filtroAves by remember { mutableStateOf(false) }
//    var filtroOutros by remember { mutableStateOf(false) }
//
//    // LISTA DE PETS
//    val petsList = remember {
//        mutableStateListOf(
//            Pet(1, "Ludovico", "Pelo curto BR", "Rua ***", "****",
//                android.R.drawable.ic_menu_gallery, "cachorro", "perdidos", "Minha rua"),
//            Pet(2, "Sarapatel", "Europeu", "Rua ***", "****",
//                android.R.drawable.ic_menu_gallery, "cachorro", "adocao", "Minha rua"),
//            Pet(3, "Snowbell", "Persa", "Rua ***", "****",
//                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Minha rua"),
//            Pet(4, "Luciano", "Doméstico", "Rua ***", "****",
//                android.R.drawable.ic_menu_gallery, "ave", "adocao", "Minha rua"),
//            Pet(5, "Leãonardo", "Curto", "Rua ***", "****",
//                android.R.drawable.ic_menu_gallery, "gato", "perdidos", "Minha rua"),
//            Pet(6, "Diana", "Bombaim", "Rua ***", "****",
//                android.R.drawable.ic_menu_gallery, "outro", "perdidos", "Minha rua"),
//            Pet(7, "Thor", "SRD", "Rua X", "****",
//                android.R.drawable.ic_menu_gallery, "cachorro", "encontrados", "Minha rua")
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                navigationIcon = {
//                    IconButton(onClick = { /*  */ }) {
//                        Icon(
//                            imageVector = Icons.Default.Menu,
//                            contentDescription = "Menu",
//                            tint = Color.Black,
//                            modifier = Modifier.size(32.dp)
//                        )
//                    }
//                },
//
//                // titulo
//                title = {
//                    Text(
//                        "FIND'EM",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        // Faz o texto preencher a largura restante
//                        modifier = Modifier.fillMaxWidth(),
//                        // Centraliza o texto dentro do espaço que ele ocupa
//                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
//                    )
//                },
//
//                // 3. Ações (Lado Direito)
//                actions = {
//                    IconButton(onClick = { /*  */ }) {
//                        Icon(
//                            imageVector = Icons.Default.LocationOn,
//                            contentDescription = "Mapa",
//                            tint = Color.Black,
//                            modifier = Modifier.size(32.dp)
//                        )
//                    }
//                },
//
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
//            )
//        },
//
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { abrirFormulario = true },
//                containerColor = Color(0xFF4CAF50)
//            ) {
//                Icon(Icons.Default.Add, null, tint = Color.White)
//            }
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .background(Color(0xFFF5F5F5))
//        ) {
//
//            // Tabs
//            TabRow(selectedTabIndex = selectedTab) {
//                tabs.forEachIndexed { i, t ->
//                    Tab(
//                        selected = selectedTab == i,
//                        onClick = { selectedTab = i },
//                        text = { Text(t) }
//                    )
//                }
//            }
//
//            // Filtros
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                FilterChip(filtroCachorros, "Cachorros") { filtroCachorros = !filtroCachorros }
//                FilterChip(filtroGatos, "Gatos") { filtroGatos = !filtroGatos }
//                FilterChip(filtroAves, "Aves") { filtroAves = !filtroAves }
//                FilterChip(filtroOutros, "Outros") { filtroOutros = !filtroOutros }
//            }
//
//            // Filtragem
//            val petsFiltrados = petsList.filter { pet ->
//                val categoriaOk =
//                    (selectedTab == 0 && pet.categoria == "perdidos") ||
//                            (selectedTab == 1 && pet.categoria == "adocao") ||
//                            (selectedTab == 2 && pet.categoria == "encontrados")
//
//                if (!categoriaOk) return@filter false
//
//                val nenhumFiltro =
//                    !filtroCachorros && !filtroGatos && !filtroAves && !filtroOutros
//                if (nenhumFiltro) return@filter true
//
//                (filtroCachorros && pet.especie == "cachorro") ||
//                        (filtroGatos && pet.especie == "gato") ||
//                        (filtroAves && pet.especie == "ave") ||
//                        (filtroOutros && pet.especie == "outro")
//            }
//
//            // GRID
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(petsFiltrados) { PetCard(it) }
//            }
//        }
//    }
//
//    // FORMULÁRIO — CORRIGIDO
//    if (abrirFormulario) {
//
//        var nome by remember { mutableStateOf("") }
//        var raca by remember { mutableStateOf("") }
//        var especie by remember { mutableStateOf("") }
//        var categoria by remember { mutableStateOf("") }
//        var endereco by remember { mutableStateOf("") }
//        var classificacao by remember { mutableStateOf("") }
//        var ultimolocal by remember { mutableStateOf("") }
//
//        AlertDialog(
//            onDismissRequest = { abrirFormulario = false },
//            title = { Text("Nova Postagem") },
//
//            text = {
//                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//
//                    OutlinedTextField(
//                        value = nome,
//                        onValueChange = { nome = it },
//                        label = { Text("Nome do animal") },
//                        singleLine = true
//                    )
//
//                    OutlinedTextField(
//                        value = raca,
//                        onValueChange = { raca = it },
//                        label = { Text("Raça") },
//                        singleLine = true
//                    )
//
//                    OutlinedTextField(
//                        value = especie,
//                        onValueChange = { especie = it },
//                        label = { Text("Espécie (cachorro, gato, ave, outro)") },
//                        singleLine = true
//                    )
//
//                    OutlinedTextField(
//                        value = categoria,
//                        onValueChange = { categoria = it.lowercase() },
//                        label = { Text("Categoria (perdidos, adocao, encontrados)") },
//                        singleLine = true
//                    )
//
//                    OutlinedTextField(
//                        value = endereco,
//                        onValueChange = { endereco = it },
//                        label = { Text("Endereço") },
//                        singleLine = true
//                    )
//
//                    OutlinedTextField(
//                        value = ultimolocal,
//                        onValueChange = { ultimolocal = it },
//                        label = { Text("Último local visto") },
//                        singleLine = true
//                    )
//
//                    OutlinedTextField(
//                        value = classificacao,
//                        onValueChange = { classificacao = it },
//                        label = { Text("Classificação (opcional)") },
//                        singleLine = true
//                    )
//                }
//            },
//
//            confirmButton = {
//                TextButton(onClick = {
//
//                    if (nome.isNotBlank() && especie.isNotBlank() && categoria.isNotBlank()) {
//
//                        // CRIA O NOVO PET — COMPLETO
//                        val novoPet = Pet(
//                            id = petsList.size + 1,
//                            nome = nome,
//                            raca = raca,
//                            endereco = endereco,
//                            classificacao = classificacao,
//                            imagemRes = android.R.drawable.ic_menu_gallery,
//                            especie = especie.lowercase(),
//                            categoria = categoria.lowercase(),
//                            ultimolocal = ultimolocal
//                        )
//
//                        petsList.add(novoPet)
//                    }
//
//                    abrirFormulario = false
//                }) {
//                    Text("Salvar")
//                }
//            },
//
//            dismissButton = {
//                TextButton(onClick = { abrirFormulario = false }) {
//                    Text("Cancelar")
//                }
//            }
//        )
//    }
//}
//
///* ------------------------------- COMPONENTES ---------------------------- */
//
//@Composable
//fun FilterChip(selected: Boolean, text: String, onClick: () -> Unit) {
//    AssistChip(
//        onClick = onClick,
//        label = { Text(text, fontSize = 12.sp) },
//        leadingIcon = {
//            if (selected) Icon(Icons.Default.Check, contentDescription = null)
//        }
//    )
//}
//
//@Composable
//fun PetCard(pet: Pet) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .background(Color.White)
//            .padding(8.dp)
//    ) {
//
//        Image(
//            painter = painterResource(id = pet.imagemRes),
//            contentDescription = pet.nome,
//            modifier = Modifier
//                .height(120.dp)
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(12.dp)),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(Modifier.height(6.dp))
//
//        Text(pet.nome, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
//        Text(pet.raca, fontSize = 12.sp, color = Color.DarkGray)
//        Text(pet.endereco, fontSize = 12.sp, color = Color.DarkGray)
//        Text(pet.ultimolocal, fontSize = 12.sp, color = Color.DarkGray)
//    }
//}
