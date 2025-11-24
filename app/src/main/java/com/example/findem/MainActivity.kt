package com.example.findem

//import android.R
//import android.R.attr.navigationIcon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FindEmTheme {
                val viewModel: FindEmViewModel by viewModels()
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "boasvindas") {
                    composable("boasvindas") {
                        Welcome(
                            onContinueClick = { navController.navigate("explicacao") }
                        )
                    }

                    composable("explicacao") {
                        Explication1Page(
                            onContinueClick = { navController.navigate("home") }
                        )
                    }


                    composable("home") {
                        FindEmScreen(viewModel = viewModel,
                            onMapClick = {navController.navigate("mapa")})
                    }

                    composable("mapa"){
                        MapScreen(
                            viewModel = viewModel,
                            onBackClick = {navController.popBackStack()}
                        )
                    }

                    // Futuramente: composable("formulario") { ... }
                }
            }
        }
    }
}

@Composable
fun FindEmTheme(content: @Composable () -> Unit) {
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