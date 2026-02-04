package com.example.findem.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.findem.model.Pet
//import android.content.Intent
//import android.net.Uri
//import androidx.compose.material.icons.filled.Phone
//import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardInform(pet: Pet, onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informações do Pet", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
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
                .background(Color.White)
        ) {
            // IMAGEM REAL (Suporta o código do seu amigo)
            if (pet.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = pet.imageUrl,
                    contentDescription = pet.nome,
                    modifier = Modifier.fillMaxWidth().height(350.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_camera)
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp).background(Color.LightGray))
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(pet.nome, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(pet.raca, fontSize = 20.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(20.dp))

                InfoDetail(label = "CATEGORIA", value = pet.categoria.uppercase())
                InfoDetail(label = "LOCALIZAÇÃO", value = pet.endereco)
                InfoDetail(label = "DESCRIÇÃO", value = pet.descricaoLocal)

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÃO WHATSAPP
                Button(
                    onClick = {
                        val numeroLimpo = pet.ownerContato.filter { it.isDigit() }

                        if (numeroLimpo.isNotEmpty()) {
                            val msg = "Olá, vi o post do ${pet.nome} no Find'Em e gostaria de mais informações."

                            val url = "https://api.whatsapp.com/send?phone=55$numeroLimpo&text=${Uri.encode(msg)}"

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } else {
                            android.widget.Toast.makeText(context, "Contato não disponível", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ENTRAR EM CONTATO", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InfoDetail(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Text(value, fontSize = 16.sp, color = Color.Black)
    }
}