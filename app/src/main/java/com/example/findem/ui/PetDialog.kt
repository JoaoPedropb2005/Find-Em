package com.example.findem.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.findem.model.Pet
import com.example.findem.model.FindEmViewModel // Importar ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel // Para obter o ViewModel
import com.example.findem.model.Estado // Importar modelo Estado
import com.example.findem.model.Municipio // Importar modelo Municipio


// Necessário para usar ExposedDropdownMenuBox e outros componentes experimentais
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Pet) -> Unit,
    viewModel: FindEmViewModel = viewModel() // Injetar ViewModel
) {
    var nome by remember { mutableStateOf("") }
    var raca by remember { mutableStateOf("") }

    // VARIÁVEIS PARA DROPDOWNS FIXOS
    var especie by remember { mutableStateOf("") }
    var isEspecieExpanded by remember { mutableStateOf(false) }
    val especiesOptions = listOf("Cachorro", "Gato", "Pássaro", "Roedor", "Outros")

    var categoria by remember { mutableStateOf("") }
    var isCategoriaExpanded by remember { mutableStateOf(false) }
    val categoriasOptions = listOf("Perdidos", "Adoção", "Encontrados")

    // VARIÁVEIS PARA DROPDOWNS DINÂMICOS (IBGE)
    var estadoSelecionado by remember { mutableStateOf<Estado?>(null) }
    var municipioSelecionado by remember { mutableStateOf<Municipio?>(null) }

    var isEstadoExpanded by remember { mutableStateOf(false) }
    var isMunicipioExpanded by remember { mutableStateOf(false) }

    // Obter dados dinâmicos do ViewModel (usando .value)
    val estados = viewModel.estadosIBGE.value
    val municipios = viewModel.municipiosIBGE.value

    var descricaoLocal by remember { mutableStateOf("") } // Rua/Referência

    val imagemPadraoRes = android.R.drawable.ic_menu_gallery

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Postagem") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Campos de texto simples
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, singleLine = true)
                OutlinedTextField(value = raca, onValueChange = { raca = it }, label = { Text("Raça") }, singleLine = true)

                // 1. Dropdown para Espécie (Fixo)
                DropdownMenuField(
                    label = "Espécie",
                    selectedValue = especie,
                    options = especiesOptions,
                    onSelected = { especie = it },
                    isExpanded = isEspecieExpanded,
                    onExpandedChange = { isEspecieExpanded = it }
                )

                // 2. Dropdown para Categoria (Fixo)
                DropdownMenuField(
                    label = "Categoria",
                    selectedValue = categoria,
                    options = categoriasOptions,
                    onSelected = { categoria = it },
                    isExpanded = isCategoriaExpanded,
                    onExpandedChange = { isCategoriaExpanded = it }
                )

                // 3. Dropdown para Estado do Brasil (UF) - DINÂMICO
                DropdownMenuField(
                    label = if (estados.isEmpty()) "Carregando Estados..." else "Estado (UF)",
                    selectedValue = estadoSelecionado?.sigla ?: "",
                    options = estados.map { it.sigla },
                    onSelected = { sigla ->
                        estadoSelecionado = estados.find { it.sigla == sigla }
                        municipioSelecionado = null // Resetar município ao trocar estado
                        viewModel.fetchMunicipios(sigla) // Carrega cidades
                    },
                    isExpanded = isEstadoExpanded,
                    onExpandedChange = { isEstadoExpanded = it },
                    enabled = estados.isNotEmpty()
                )

                // 4. Dropdown para Município (Cidade) - DINÂMICO
                DropdownMenuField(
                    label = if (estadoSelecionado == null) "Selecione o Estado" else if (municipios.isEmpty()) "Carregando Cidades..." else "Cidade",
                    selectedValue = municipioSelecionado?.nome ?: "",
                    options = municipios.map { it.nome },
                    onSelected = { nome ->
                        municipioSelecionado = municipios.find { it.nome == nome }
                    },
                    isExpanded = isMunicipioExpanded,
                    onExpandedChange = { isMunicipioExpanded = it },
                    enabled = municipios.isNotEmpty()
                )

                // Campos de texto simples: Rua / Ponto de Referência
                OutlinedTextField(value = descricaoLocal, onValueChange = { descricaoLocal = it }, label = { Text("Rua / Ponto de Referência") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val enderecoCompleto = if (municipioSelecionado != null && estadoSelecionado != null) {
                        "${descricaoLocal.trim()}, ${municipioSelecionado!!.nome}, ${estadoSelecionado!!.sigla}"
                    } else {
                        // Se não selecionou cidade/estado, usa a descrição local como fallback
                        descricaoLocal.trim()
                    }

                    if (nome.isNotBlank() && especie.isNotBlank() && categoria.isNotBlank() && enderecoCompleto.isNotBlank()) {
                        val novoPet = Pet(
                            id = (System.currentTimeMillis() % 10000).toInt(),
                            nome = nome,
                            raca = raca,
                            endereco = enderecoCompleto, // Endereço formatado para Geocoding
                            classificacao = "",
                            imagemRes = imagemPadraoRes,
                            especie = especie.trim().lowercase(),
                            categoria = categoria.trim().lowercase(),
                            descricaoLocal = descricaoLocal, // Mantém a rua/referência aqui
                            latitude = 0.0,
                            longitude = 0.0
                        )
                        onConfirm(novoPet)
                    }
                }
            ) {
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

// =================================================================
// COMPONENTE AUXILIAR DROPDOWN MENU (Habilitado para o estado)
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    enabled: Boolean = true // Adicionado 'enabled' para desabilitar o dropdown de cidade
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { if(enabled) onExpandedChange(it) },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = { /* Não permite digitação direta */ },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded && enabled) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = isExpanded && enabled,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelected(selectionOption)
                        onExpandedChange(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}