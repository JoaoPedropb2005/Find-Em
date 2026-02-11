package com.example.findem

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import com.example.findem.ui.*
import com.example.findem.ui.theme.FindEmScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alertas Find'Em"
            val descriptionText = "Notificações de pets por perto"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("PET_ALERT", name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        setContent {
            FindEmThemeCustom {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: FindEmViewModel by viewModels()
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { }

                    LaunchedEffect(Unit) {
                        val permissions = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        // Permissão necessária para Android 13+
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        permissionLauncher.launch(permissions.toTypedArray())

                        val serviceIntent = Intent(context, FindEmService::class.java)
                        context.startService(serviceIntent)
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
                                    viewModel.petSelecionadoParaDetalhes = pet
                                    navController.navigate("card_inform")
                                }
                            )
                        }

                        composable(FindEmRoute.MAP) {
                            MapScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() },
                                onNavigateToDetails = { navController.navigate("card_inform") }
                            )
                        }

                        composable(FindEmRoute.SETTINGS) {
                            ConfigurationScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("minhas_postagens") {
                            MyPostScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onPetClick = { navController.navigate("pet_details") }
                            )
                        }

                        composable("pet_details") {
                            PetDetailsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("card_inform") {
                            val pet = viewModel.petSelecionadoParaDetalhes
                            if (pet != null) {
                                CardInform(
                                    pet = pet,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() },
                                    onFavoriteClick = { viewModel.toggleFavorito(pet) }
                                )
                            }
                        }

                        composable("favorites") {
                            FavoritesScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onPetClick = { navController.navigate("card_inform") }
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