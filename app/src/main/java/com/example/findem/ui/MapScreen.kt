package com.example.findem.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
/*import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically*/
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
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


//commit
//private val Icons.Filled.NotificationsActive: Any

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FindEmViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var isNotificacoesExpanded by remember { mutableStateOf(true) }

    // Posição inicial (Recife)
    val recife = LatLng(-8.0476, -34.8770)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recife, 12f)
    }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        )
    }
    var properties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    viewModel.updateUserLocation(location.latitude, location.longitude)
                }
            }
        }
    }

    fun startLocationUpdates() {
        try {
            val hasFine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                // Ativa visual do mapa
                properties = properties.copy(isMyLocationEnabled = true)
                uiSettings = uiSettings.copy(myLocationButtonEnabled = true)

                // Configura o pedido de atualização (a cada 5 segundos ou 10 metros)
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setMinUpdateDistanceMeters(10f)
                    .build()

                // Começa a escutar
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )

                // Dá um zoom inicial na posição atual (uma única vez)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateUserLocation(loc.latitude, loc.longitude)
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    loc.latitude,
                                    loc.longitude
                                ), 14f
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapScreen", "Erro de permissão: ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        startLocationUpdates()
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mapa de Buscas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        // --- MAPA ---
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
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
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "FILTROS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterCheckbox("Cachorros", viewModel.mapFiltroCachorros)
                        FilterCheckbox("Gatos", viewModel.mapFiltroGatos)
                        FilterCheckbox("Aves", viewModel.mapFiltroAves)
                        FilterCheckbox("Outros", viewModel.mapFiltroOutros)
                    }

                    Divider(color = Color.LightGray, thickness = 1.dp)

                    // --- SEÇÃO DE NOTIFICAÇÕES ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable {
                                isNotificacoesExpanded = !isNotificacoesExpanded
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "NOTIFICAÇÕES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Icon(
                            if (isNotificacoesExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    AnimatedVisibility(visible = isNotificacoesExpanded) {
                        if (viewModel.notificacoesProximas.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 250.dp),
                                contentPadding = PaddingValues(top = 8.dp)
                            ) {
                                items(viewModel.notificacoesProximas) { notif ->
                                    NotificacaoItem(notif)
                                }
                            }
                        } else {
                            Text(
                                "Nenhum alerta próximo no momento.",
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
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
        Checkbox(
            checked = state.value,
            onCheckedChange = { state.value = it },
            colors = CheckboxDefaults.colors(checkedColor = Color.Black)
        )
        Text(label, fontSize = 10.sp)
    }
}

@Composable
fun NotificacaoItem(notificacao: Notificacao) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = notificacao.mensagem,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Text(
                text = notificacao.distancia,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}