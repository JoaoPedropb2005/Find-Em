package com.example.findem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.findem.model.FindEmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreen(
    viewModel: FindEmViewModel,
    onBack: () -> Unit
) {

    val pet = viewModel.petSelecionadoParaDetalhes

    val isAdocao = pet?.categoria?.equals("adoção", ignoreCase = true) == true ||
            pet?.categoria?.equals("adocao", ignoreCase = true) == true

    val isEncontrado = pet?.categoria?.equals("encontrados", ignoreCase = true) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdocao) "Quero um Lar" else "Detalhes da Busca") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isAdocao) Color(0xFF673AB7) else Color(0xFF4CAF50), // Roxo para Adoção, Verde para Perdidos
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (pet == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Erro ao carregar detalhes.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Foto Grande
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray)
                ) {
                    if (pet.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = pet.imageUrl,
                            contentDescription = pet.nome,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).align(Alignment.Center),
                            tint = Color.Gray
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(50),
                        color = if (isEncontrado) Color.Green else if (isAdocao) Color(0xFF673AB7) else Color(
                            0xFFD32F2F
                        )
                    ) {
                        Text(
                            text = if (isEncontrado) "RESOLVIDO" else pet.categoria.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {

                    // --- CABEÇALHO (Nome e Raça) ---
                    Text(
                        text = pet.nome,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "${pet.especie.capitalize()} • ${pet.raca}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isAdocao) {
                        val tags = pet.classificacao.split("|").map { it.trim() }
                            .filter { it.isNotEmpty() }

                        if (tags.isNotEmpty()) {
                            Text("Perfil do Pet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Exibe as tags em linhas (Layout simples)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Tenta pegar as 3 primeiras tags (Sexo, Porte, Idade)
                                tags.take(3).forEach { tag ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(tag) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Info,
                                                null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                            }

                            // Exibe o resto (Saúde: Castrado, Vacinado, etc)
                            if (tags.size > 3) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    tags.drop(3).forEach { tag ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(tag, fontSize = 12.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = Color(0xFFE8F5E9),
                                                labelColor = Color(0xFF2E7D32)
                                            ),
                                            border = null
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    DetalheLinha(Icons.Default.LocationOn, pet.endereco, "Localização")

                    Spacer(modifier = Modifier.height(16.dp))

                    if (pet.descricaoLocal.isNotBlank()) {
                        val tituloDescricao =
                            if (isAdocao) "Sobre o ${pet.nome}" else "Ponto de Referência / Detalhes"
                        Text(tituloDescricao, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pet.descricaoLocal,
                            color = Color.DarkGray,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    if (!isEncontrado) {
                        val buttonColor = if (isAdocao) Color(0xFF673AB7) else Color(0xFF4CAF50)
                        val buttonText =
                            if (isAdocao) "MARCAR COMO ADOTADO" else "MARCAR COMO ENCONTRADO"
                        val buttonIcon =
                            if (isAdocao) Icons.Default.Home else Icons.Default.CheckCircle
                        val helperText =
                            if (isAdocao) "O animal sairá da lista de adoção." else "O animal sairá da lista de perdidos."

                        Button(
                            onClick = {
                                viewModel.marcarComoEncontrado(pet) {
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(6.dp)
                        ) {
                            Icon(buttonIcon, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = helperText,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFF4CAF50)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                                Spacer(Modifier.width(8.dp))
                                val textoFinal =
                                    if (isAdocao) "Este animal já foi adotado!" else "Este animal foi encontrado!"
                                Text(
                                    textoFinal,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetalheLinha(icon: ImageVector, text: String, label: String? = null) {
    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            if (label != null) {
                Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            Text(text, fontSize = 16.sp, color = Color.Black)
        }
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}