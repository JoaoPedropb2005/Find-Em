package com.example.findem.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.model.FindEmViewModel
import com.example.findem.model.Notificacao
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

//private val Icons.Filled.NotificationsActive: Any

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FindEmViewModel,
    onBackClick: () -> Unit
) {
    var isNotificacoesExpanded by remember { mutableStateOf(false) }

    // Posição inicial (Recife)
    val recife = LatLng(-8.0476, -34.8770)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recife, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Buscas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- MAPA ---
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    viewModel.pets.forEach { pet ->
                        if (pet.latitude != 0.0 && pet.longitude != 0.0) {
                            Marker(
                                state = MarkerState(position = LatLng(pet.latitude, pet.longitude)),
                                title = pet.nome,
                                snippet = "${pet.raca} - ${pet.categoria}"
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("FILTROS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterCheckbox(viewModel.mapFiltroCachorros.value, "Cachorros") { viewModel.mapFiltroCachorros.value = !viewModel.mapFiltroCachorros.value }
                        FilterCheckbox(viewModel.mapFiltroGatos.value, "Gatos") { viewModel.mapFiltroGatos.value = !viewModel.mapFiltroGatos.value }
                        FilterCheckbox(viewModel.mapFiltroOutros.value, "Outros") { viewModel.mapFiltroOutros.value = !viewModel.mapFiltroOutros.value }
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("NOTIFICAÇÕES", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        IconButton(onClick = { isNotificacoesExpanded = !isNotificacoesExpanded }) {
                            Icon(
                                if (isNotificacoesExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                null, tint = Color.Gray
                            )
                        }
                    }

                    AnimatedVisibility(visible = isNotificacoesExpanded) {
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(viewModel.notificacoes) { notif ->
                                NotificacaoItem(notif)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterCheckbox(checked: Boolean, label: String, onCheckedChange: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
        )
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun NotificacaoItem(notificacao: Notificacao) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Notifications, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(notificacao.mensagem, fontSize = 13.sp)
            Text(notificacao.distancia, fontSize = 11.sp, color = Color.Gray)
        }
    }
}