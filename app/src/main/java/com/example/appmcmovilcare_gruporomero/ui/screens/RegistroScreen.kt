package com.example.appmcmovilcare_gruporomero.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: UsuarioViewModel,
    onNavegarAResumen: () -> Unit,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    var evidenceError by remember { mutableStateOf<String?>(null) }
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    fun createEvidenceFile(): File {
        val directory = File(context.filesDir, "evidencias")
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("No se pudo preparar el almacenamiento de evidencias")
        }
        return File(directory, "evidencia_${System.currentTimeMillis()}.jpg")
    }

    fun createEvidenceUri(file: File): Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        val photoFile = pendingPhotoFile
        pendingPhotoFile = null
        if (saved && photoFile != null && photoFile.isFile && photoFile.length() > 0) {
            evidenceError = null
            viewModel.onImagenCapturada(createEvidenceUri(photoFile).toString())
        } else {
            photoFile?.delete()
            evidenceError = if (saved) {
                "La cámara no generó una foto válida. Intenta nuevamente o selecciona una imagen de la galería."
            } else {
                "No se tomó ninguna foto. Intenta nuevamente o selecciona una imagen de la galería."
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri ->
        if (selectedUri != null) {
            try {
                val file = createEvidenceFile()
                val copied = context.contentResolver.openInputStream(selectedUri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                } ?: throw IOException("No se pudo leer la imagen seleccionada")
                if (copied > 0 && file.length() > 0) {
                    evidenceError = null
                    viewModel.onImagenCapturada(createEvidenceUri(file).toString())
                } else {
                    evidenceError = "La imagen seleccionada está vacía. Intenta con otra foto."
                }
            } catch (_: Exception) {
                evidenceError = "No se pudo adjuntar la imagen. Intenta seleccionar otra foto."
            }
        }
    }

    fun tomarFoto() {
        evidenceError = null
        try {
            val file = createEvidenceFile()
            pendingPhotoFile = file
            cameraLauncher.launch(createEvidenceUri(file))
        } catch (_: Exception) {
            pendingPhotoFile?.delete()
            pendingPhotoFile = null
            evidenceError = "No se pudo abrir la cámara. Verifica que haya una app de cámara instalada o selecciona una imagen de la galería."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MCmovil Care - Nueva Solicitud") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ingreso de Equipo para Diagnóstico",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = state.clienteNombre,
                onValueChange = viewModel::onNombreChanged,
                label = { Text("Nombre del Cliente") },
                isError = state.errorNombre != null,
                supportingText = { state.errorNombre?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.correoCliente,
                onValueChange = viewModel::onCorreoChanged,
                label = { Text("Correo Electrónico") },
                isError = state.errorCorreo != null,
                supportingText = { state.errorCorreo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.equipoModelo,
                onValueChange = viewModel::onModeloChanged,
                label = { Text("Modelo del Equipo (ej. Mac Book Pro)") },
                isError = state.errorModelo != null,
                supportingText = { state.errorModelo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.numeroSerie,
                onValueChange = viewModel::onSerieChanged,
                label = { Text("Número de Serie") },
                isError = state.errorSerie != null,
                supportingText = { state.errorSerie?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = "Describa los problemas que presenta el equipo:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = state.diagnostico,
                onValueChange = viewModel::onDiagnosticoChanged,
                label = { Text("Síntomas / Motivo de Atención") },
                placeholder = {
                    Text(
                        "Ej: No enciende, pantalla negra, se calienta mucho, " +
                        "no reconoce disco duro, hace ruidos extraños..."
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text(
                text = "Seleccione los síntomas que presenta el equipo:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            var sintomasSeleccionados by remember { mutableStateOf(setOf<String>()) }

            val sintomasComunes = listOf(
                "No enciende",
                "Pantalla rota",
                "Se calienta",
                "Lento",
                "No carga",
                "Hace ruido"
            )

            // Actualizar el campo de diagnóstico cuando cambian las selecciones
            LaunchedEffect(sintomasSeleccionados) {
                val diagnosticoActual = state.diagnostico
                val nuevosSintomas = sintomasSeleccionados.joinToString(", ")
                if (diagnosticoActual.isNotEmpty() && diagnosticoActual != nuevosSintomas) {
                    Unit
                } else {
                    viewModel.onDiagnosticoChanged(nuevosSintomas)
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sintomasComunes) { sintoma ->
                    val seleccionado = sintomasSeleccionados.contains(sintoma)
                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            sintomasSeleccionados = if (seleccionado) {
                                sintomasSeleccionados - sintoma
                            } else {
                                sintomasSeleccionados + sintoma
                            }
                        },
                        label = {
                            Text(
                                text = sintoma,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingIcon = {
                            if (seleccionado) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Text(
                text = "Puede seleccionar varios síntomas o escribir manualmente abajo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )


            Text(
                text = "Foto de evidencia (opcional):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            evidenceError?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Button(
                onClick = { tomarFoto() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (state.imagenUri == null) "Tomar foto de evidencia" else "Cambiar foto capturada")
            }

            Text(
                text = "Se abrirá la cámara del dispositivo; al confirmar, la foto se adjuntará a la solicitud.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar imagen de la galería")
            }


            AnimatedVisibility(
                visible = state.imagenUri != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        state.imagenUri?.let { uriString ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(uriString)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto de evidencia",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓ Foto adjuntada correctamente",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = {
                                viewModel.onImagenEliminada()
                            }) {
                                Text("✕ Eliminar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            state.errorRegistro?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { viewModel.guardarOrden(onNavegarAResumen) },
                enabled = state.esFormularioValido && !state.cargandoRegistro,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.cargandoRegistro) {
                    CircularProgressIndicator()
                } else {
                    Text("Registrar e Ingresar Orden")
                }
            }
        }
    }
}
