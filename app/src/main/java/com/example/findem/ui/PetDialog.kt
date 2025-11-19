package com.example.findem.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.findem.model.Pet

@Composable
fun PetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Pet) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var raca by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var descricaoLocal by remember { mutableStateOf("") } // Era ultimolocal no antigo

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Postagem") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, singleLine = true)
                OutlinedTextField(value = raca, onValueChange = { raca = it }, label = { Text("Raça") }, singleLine = true)
                OutlinedTextField(value = especie, onValueChange = { especie = it }, label = { Text("Espécie (cachorro, gato...)") }, singleLine = true)
                OutlinedTextField(value = categoria, onValueChange = { categoria = it.lowercase() }, label = { Text("Categoria (perdidos/adocao)") }, singleLine = true)
                OutlinedTextField(value = endereco, onValueChange = { endereco = it }, label = { Text("Endereço") }, singleLine = true)
                OutlinedTextField(value = descricaoLocal, onValueChange = { descricaoLocal = it }, label = { Text("Último local visto") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nome.isNotBlank() && especie.isNotBlank()) {
                    val novoPet = Pet(
                        id = (System.currentTimeMillis() % 10000).toInt(),
                        nome = nome,
                        raca = raca,
                        endereco = endereco,
                        classificacao = "",
                        imagemRes = android.R.drawable.ic_menu_gallery,
                        especie = especie.trim().lowercase(),
                        categoria = categoria.trim().lowercase(),
                        descricaoLocal = descricaoLocal
                    )
                    onConfirm(novoPet)
                }
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}