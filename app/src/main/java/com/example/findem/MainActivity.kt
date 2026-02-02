package com.example.findem

//import android.R
//import android.R.attr.navigationIcon
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findem.model.FindEmViewModel
import com.example.findem.ui.MapScreen
import com.example.findem.ui.theme.FindEmScreen
import com.example.findem.ui.FindEmRoute
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: FindEmViewModel by viewModels()

        setContent {
            FindEmThemeCustom {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val viewModel: FindEmViewModel by viewModels()

                    // --- PEDIDO DE PERMISSÃO AO INICIAR O APP ---
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        val granted =
                            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                        // Se quiser, pode logar ou avisar o ViewModel aqui
                    }

                    LaunchedEffect(Unit) {
                        // Verifica se já tem permissão. Se não tiver, pede.
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }

                    val destinoInicial = intent.getStringExtra("destino_inicial") ?: "boasvindas"

                    NavHost(navController = navController, startDestination = destinoInicial) {
                        composable(FindEmRoute.WELCOME) {
                            Welcome(
                                onContinueClick = { navController.navigate(FindEmRoute.EXPLICATION) }
                            )
                        }

                        composable(FindEmRoute.EXPLICATION) {
                            Explication1Page(
                                onContinueClick = {
                                    navController.navigate(FindEmRoute.HOME) {
                                        popUpTo(FindEmRoute.WELCOME) { inclusive = true }
                                    }
                                }
                            )
                        }


                        composable(FindEmRoute.HOME) {
                            FindEmScreen(
                                viewModel = viewModel,
                                onMapClick = { navController.navigate(FindEmRoute.MAP) })
                        }

                        composable(FindEmRoute.MAP) {
                            MapScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun FindEmThemeCustom(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4CAF50),
            onPrimary = Color.White,
            background = Color(0xFFF5F5F5),
            onBackground = Color.Black
        ),
        content = content
    )
}