package com.example.findem.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.findem.model.Pet
import com.example.findem.model.FindEmViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.findem.model.Estado
import com.example.findem.model.Municipio
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDialog(
    petParaEditar: Pet? = null,
    onDismiss: () -> Unit,
    onConfirm: (Pet) -> Unit,
    viewModel: FindEmViewModel = viewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val userEmail = viewModel.currentUser?.email ?: "anonimo@findem.com"

    val adaptiveImageHeight = if (screenHeight < 640.dp) 120.dp else 160.dp

    var nome by remember { mutableStateOf(petParaEditar?.nome ?: "") }
    var raca by remember { mutableStateOf(petParaEditar?.raca ?: "") }
    var contato by remember { mutableStateOf(petParaEditar?.ownerContato ?: "") }
    var especie by remember { mutableStateOf(petParaEditar?.especie ?: "") }
    var categoria by remember { mutableStateOf(petParaEditar?.categoria ?: "") }
    var descricaoLocal by remember { mutableStateOf(petParaEditar?.descricaoLocal ?: "") }

    val dadosAdocao = petParaEditar?.classificacao?.split("|")?.map { it.trim() } ?: emptyList()
    var sexoAdocao by remember { mutableStateOf(dadosAdocao.find { it == "Macho" || it == "Fêmea" } ?: "Macho") }
    var porteAdocao by remember { mutableStateOf(dadosAdocao.find { it == "Pequeno" || it == "Médio" || it == "Grande" } ?: "Médio") }
    var idadeAdocao by remember { mutableStateOf(dadosAdocao.find { it == "Filhote" || it == "Adulto" || it == "Idoso" } ?: "Adulto") }

    var castrado by remember { mutableStateOf(dadosAdocao.contains("Castrado")) }
    var vacinado by remember { mutableStateOf(dadosAdocao.contains("Vacinado")) }
    var vermifugado by remember { mutableStateOf(dadosAdocao.contains("Vermifugado")) }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(if (petParaEditar?.imageUrl?.isNotEmpty() == true) Uri.parse(petParaEditar.imageUrl) else null)
    }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceOption by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success && tempCameraUri != null) selectedImageUri = tempCameraUri }
    )

    val especiesOptions = listOf("Cachorro", "Gato", "Ave", "Outros")
    val categoriasOptions = listOf("Perdidos", "Adoção", "Encontrados")
    var isEspecieExpanded by remember { mutableStateOf(false) }
    var isCategoriaExpanded by remember { mutableStateOf(false) }
    var isEstadoExpanded by remember { mutableStateOf(false) }
    var isMunicipioExpanded by remember { mutableStateOf(false) }

    val estados = viewModel.estadosIBGE.value
    val municipios = viewModel.municipiosIBGE.value
    var estadoSelecionado by remember { mutableStateOf<Estado?>(null) }
    var municipioSelecionado by remember { mutableStateOf<Municipio?>(null) }

    LaunchedEffect(petParaEditar) {
        if (petParaEditar != null) {
            val parts = petParaEditar.endereco.split(",")
            if (parts.size >= 3) {
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (petParaEditar == null) "Nova Postagem" else "Editar Postagem") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(adaptiveImageHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.LightGray)
                        .clickable { showImageSourceOption = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Gray)
                            Text("Adicionar foto", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                DropdownMenuField("Categoria", categoria, categoriasOptions, { categoria = it }, isCategoriaExpanded, { isCategoriaExpanded = it })

                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do Animal", fontSize = 15.sp) }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        DropdownMenuField("Espécie", especie, especiesOptions, { especie = it }, isEspecieExpanded, { isEspecieExpanded = it })
                    }
                    OutlinedTextField(value = raca, onValueChange = { raca = it }, label = { Text("Raça") }, modifier = Modifier.weight(1f))
                }

                Divider(Modifier.padding(vertical = 4.dp))

                // forms mudando pra adoção
                if (categoria == "Adoção") {
                    Text("Perfil da Adoção", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sexo:", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold)
                        listOf("Macho", "Fêmea").forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                                RadioButton(selected = (sexoAdocao == item), onClick = { sexoAdocao = item })
                                Text(item)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) {
                            DropdownMenuField("Porte", porteAdocao, listOf("Pequeno", "Médio", "Grande"), { porteAdocao = it }, false, {})
                        }
                        Box(Modifier.weight(1f)) {
                            DropdownMenuField("Idade", idadeAdocao, listOf("Filhote", "Adulto", "Idoso"), { idadeAdocao = it }, false, {})
                        }
                    }

                    Column {
                        Text("Cuidados:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            FilterChip(selected = castrado, onClick = { castrado = !castrado }, label = { Text("Castrado") })
                            FilterChip(selected = vacinado, onClick = { vacinado = !vacinado }, label = { Text("Vacinado") })
                            FilterChip(selected = vermifugado, onClick = { vermifugado = !vermifugado }, label = { Text("Vermif.") })
                        }
                    }

                    OutlinedTextField(
                        value = descricaoLocal,
                        onValueChange = { descricaoLocal = it },
                        label = { Text("História / Sobre o animal") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 5
                    )

                } else {
                    // --- FORMULÁRIO PERDIDOS ---
                    Text("Detalhes do Desaparecimento", fontWeight = FontWeight.Bold, color = Color.Red)

                    OutlinedTextField(
                        value = descricaoLocal,
                        onValueChange = { descricaoLocal = it },
                        label = { Text("Ponto de Referência / Rua") },
                        placeholder = { Text("Ex: Próximo à padaria central") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // --- LOCALIZAÇÃO (IBGE) ---
                Text("Localização", fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    label = if (estados.isEmpty()) "Carregando..." else "Estado (UF)",
                    selectedValue = estadoSelecionado?.sigla ?: "",
                    options = estados.map { it.sigla },
                    onSelected = { sigla ->
                        estadoSelecionado = estados.find { it.sigla == sigla }
                        municipioSelecionado = null
                        viewModel.fetchMunicipios(sigla)
                    },
                    isExpanded = isEstadoExpanded,
                    onExpandedChange = { isEstadoExpanded = it }
                )

                DropdownMenuField(
                    label = "Cidade",
                    selectedValue = municipioSelecionado?.nome ?: "",
                    options = municipios.map { it.nome },
                    onSelected = { nomeMun -> municipioSelecionado = municipios.find { it.nome == nomeMun } },
                    isExpanded = isMunicipioExpanded,
                    onExpandedChange = { isMunicipioExpanded = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val enderecoBase = if (municipioSelecionado != null && estadoSelecionado != null) {
                        "${municipioSelecionado!!.nome}, ${estadoSelecionado!!.sigla}"
                    } else { "Brasil" } // Fallback

                    val enderecoCompleto = if (categoria == "Perdidos") {
                        "${descricaoLocal.trim()}, $enderecoBase"
                    } else {
                        enderecoBase
                    }

                    val classificacaoFinal = if (categoria == "Adoção") {
                        val tags = mutableListOf(sexoAdocao, porteAdocao, idadeAdocao)
                        if (castrado) tags.add("Castrado")
                        if (vacinado) tags.add("Vacinado")
                        if (vermifugado) tags.add("Vermifugado")
                        tags.joinToString(" | ")
                    } else {
                        "Perdido"
                    }

                    if (nome.isNotBlank() && especie.isNotBlank() && categoria.isNotBlank()) {
                        val petFinal = (petParaEditar ?: Pet()).copy(
                            id = petParaEditar?.id ?: "", // ViewModel gera se vazio
                            ownerEmail = userEmail,
                            ownerContato = viewModel.userWhatsapp,
                            nome = nome,
                            raca = raca,
                            endereco = enderecoCompleto,
                            imageUrl = selectedImageUri?.toString() ?: "",
                            especie = especie.trim().lowercase(),
                            categoria = categoria.trim().lowercase(),
                            descricaoLocal = descricaoLocal, // História ou Referência
                            classificacao = classificacaoFinal, // Metadados da adoção
                            userId = viewModel.currentUser?.uid ?: ""
                        )
                        onConfirm(petFinal)
                    }
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )

    if (showImageSourceOption) {
        AlertDialog(
            onDismissRequest = { showImageSourceOption = false },
            title = { Text("Escolher Imagem") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Tirar Foto") },
                        leadingContent = { Icon(Icons.Default.PhotoCamera, null) },
                        modifier = Modifier.clickable {
                            showImageSourceOption = false
                            val uri = criarUriParaCamera(context)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Galeria") },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                        modifier = Modifier.clickable {
                            showImageSourceOption = false
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showImageSourceOption = false }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label,
                fontSize = 14.sp
            )},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

fun criarUriParaCamera(context: Context): Uri {
    val directory = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File.createTempFile("selected_image_", ".jpg", directory)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}