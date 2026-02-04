package com.example.findem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findem.model.FindEmViewModel
import com.example.findem.ui.MapScreen
import com.example.findem.ui.theme.FindEmScreen
import com.example.findem.ui.FindEmRoute
import com.example.findem.ui.CardInform
import com.example.findem.ui.MyPostScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FindEmThemeCustom {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: FindEmViewModel by viewModels()
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    // Pedido de permissão de localização
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { }

                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    }

                    val destinoInicial = intent.getStringExtra("destino_inicial") ?: FindEmRoute.WELCOME

                    NavHost(navController = navController, startDestination = destinoInicial) {
                        composable(FindEmRoute.WELCOME) {
                            Welcome(onContinueClick = { navController.navigate(FindEmRoute.EXPLICATION) })
                        }

                        composable(FindEmRoute.EXPLICATION) {
                            Explication1Page(onContinueClick = {
                                navController.navigate(FindEmRoute.HOME) {
                                    popUpTo(FindEmRoute.WELCOME) { inclusive = true }
                                }
                            })
                        }

                        composable(FindEmRoute.HOME) {
                            FindEmScreen(
                                viewModel = viewModel,
                                navController = navController,
                                onMapClick = { navController.navigate(FindEmRoute.MAP) },
                                onPetClick = { pet ->
                                    navController.navigate("detalhes/${pet.id}")
                                }
                            )
                        }

                        composable(FindEmRoute.MAP) {
                            MapScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() })
                        }

                        // ROTA DE DETALHES
                        composable("detalhes/{petId}") { backStackEntry ->
                            val petId = backStackEntry.arguments?.getString("petId")
                            val pet = viewModel.pets.find { it.id == petId }
                            pet?.let {
                                CardInform(pet = it, onBack = { navController.popBackStack() })
                            }
                        }
                        composable("minhas_postagens") {
                            MyPostScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
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