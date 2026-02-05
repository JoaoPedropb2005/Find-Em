package com.example.findem.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.findem.model.FindEmViewModel
import com.example.findem.model.Notificacao
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FindEmViewModel,
    onBackClick: () -> Unit,
    onNavigateToDetails: () -> Unit
) {
    val context = LocalContext.current
    var isNotificacoesExpanded by remember { mutableStateOf(true) }

    val recife = LatLng(-8.0476, -34.8770)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recife, 12f)
    }

    var uiSettings by remember {
        mutableStateOf(MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false))
    }
    var properties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    // AJUSTADO: Certifique-se que o nome no ViewModel é este
                    viewModel.updateUserLocation(location.latitude, location.longitude)
                }
            }
        }
    }

    fun startLocationUpdates() {
        try {
            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (hasFine) {
                properties = properties.copy(isMyLocationEnabled = true)
                uiSettings = uiSettings.copy(myLocationButtonEnabled = true)

                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setMinUpdateDistanceMeters(10f)
                    .build()

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

                fusedLocationClient.lastLocation.addOnSuccessListener { loc: android.location.Location? ->
                    if (loc != null) {
                        viewModel.updateUserLocation(loc.latitude, loc.longitude)
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 14f))
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapScreen", "Erro: ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        startLocationUpdates()
        onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
            ) {
                viewModel.petsFiltradosMap.forEach { pet ->
                    if (pet.latitude != 0.0 && pet.longitude != 0.0) {
                        Marker(
                            state = MarkerState(position = LatLng(pet.latitude, pet.longitude)),
                            title = pet.nome,
                            snippet = "${pet.raca} - ${pet.categoria}"
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().shadow(10.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("FILTROS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        // AJUSTADO: Usando os filtros globais do ViewModel
                        FilterCheckbox("Cachorros", viewModel.filtroCachorros)
                        FilterCheckbox("Gatos", viewModel.filtroGatos)
                        FilterCheckbox("Aves", viewModel.filtroAves)
                        FilterCheckbox("Outros", viewModel.filtroOutros)
                    }

                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { isNotificacoesExpanded = !isNotificacoesExpanded }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("ALERTAS (Raio 1km)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Icon(if (isNotificacoesExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, null, tint = Color.Gray)
                    }

                    AnimatedVisibility(visible = isNotificacoesExpanded) {
                        if (viewModel.notificacoesProximas.isNotEmpty()) {
                            LazyColumn(modifier = Modifier.heightIn(max = 200.dp), contentPadding = PaddingValues(top = 8.dp)) {
                                items(viewModel.notificacoesProximas) { notif ->
                                    NotificacaoItem(
                                        notificacao = notif,
                                        onClick = {
                                            // Busca o pet pelo ID salvo na notificação
                                            val pet = viewModel.getPetById(notif.petId)
                                            if (pet != null) {
                                                viewModel.petSelecionadoParaDetalhes = pet
                                                onNavigateToDetails()
                                            }
                                        }
                                    )
                                }
                            }
                        } else {
                            Text("Nenhum alerta em 1km.", modifier = Modifier.padding(16.dp).fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterCheckbox(label: String, state: MutableState<Boolean>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = state.value, onCheckedChange = { state.value = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50)))
        Text(label, fontSize = 10.sp)
    }
}

@Composable
fun NotificacaoItem(notificacao: Notificacao, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.ErrorOutline, null, tint = Color.Black, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text = notificacao.mensagem,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
    }
}