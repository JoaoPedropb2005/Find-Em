package com.example.findem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.model.FindEmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    viewModel: FindEmViewModel,
    onBack: () -> Unit
) {
    // Estados para o formulário de edição
    var editandoPerfil by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf(viewModel.userName ?: "") }
    var whatsapp by remember { mutableStateOf(viewModel.userWhatsapp) }

    // Estado para o seletor de idioma
    var mostrarIdiomas by remember { mutableStateOf(false) }
    var idiomaSelecionado by remember { mutableStateOf("Português (BR)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8F9FA))
        ) {
            // --- CABEÇALHO PERFIL ---
            SeccaoTitulo("MEU PERFIL")

            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!editandoPerfil) {
                        // Visualização dos dados atuais
                        ItemInfo(Icons.Default.Person, "Nome", viewModel.userName ?: "Visitante")
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        ItemInfo(Icons.Default.Email, "E-mail de Login", viewModel.currentUser?.email ?: "Não logado")
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        ItemInfo(Icons.Default.Phone, "WhatsApp de Contato", viewModel.userWhatsapp.ifBlank { "Não informado" })

                        Button(
                            onClick = {
                                nome = viewModel.userName ?: ""
                                whatsapp = viewModel.userWhatsapp
                                editandoPerfil = true
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("EDITAR DADOS")
                        }
                    } else {
                        // Formulário de Edição
                        Text("Atualizar Informações", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text("Nome Completo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text("WhatsApp (com DDD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { editandoPerfil = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Cancelar") }

                            Button(
                                onClick = {
                                    viewModel.atualizarPerfil(nome, whatsapp) { sucesso ->
                                        if (sucesso) editandoPerfil = false
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Salvar") }
                        }
                    }
                }
            }

            // --- SEÇÃO IDIOMA ---
            SeccaoTitulo("PREFERÊNCIAS")

            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                ListItem(
                    headlineContent = { Text("Idioma do Aplicativo", fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(idiomaSelecionado, color = Color.Gray) },
                    leadingContent = {
                        Surface(color = Color(0xFFE8F5E9), shape = CircleShape) {
                            Icon(Icons.Default.Language, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = Color(0xFF4CAF50))
                        }
                    },
                    trailingContent = { Icon(Icons.Default.KeyboardArrowRight, null) },
                    modifier = Modifier.clickable { mostrarIdiomas = true }
                )
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "Versão 1.0.0 (Beta Presentation)",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }

    // Modal de Idiomas
    if (mostrarIdiomas) {
        AlertDialog(
            onDismissRequest = { mostrarIdiomas = false },
            title = { Text("Escolha um Idioma") },
            text = {
                Column {
                    val idiomas = listOf("Português (BR)", "English (US)", "Español")
                    idiomas.forEach { lang ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                idiomaSelecionado = lang
                                mostrarIdiomas = false
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = lang == idiomaSelecionado, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(lang, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarIdiomas = false }) { Text("CANCELAR") }
            }
        )
    }
}

@Composable
fun SeccaoTitulo(texto: String) {
    Text(
        text = texto,
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = 1.sp
    )
}

@Composable
fun ItemInfo(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = Color(0xFFF1F3F4),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(icon, null, modifier = Modifier.padding(8.dp), tint = Color.Gray)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 15.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
        }
    }
}