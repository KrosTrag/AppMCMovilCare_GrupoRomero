package com.example.appmcmovilcare_gruporomero.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    onNuevaOrden: () -> Unit
) {
    val ultimaOrden by viewModel.ultimaOrdenCreada.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MCmovil Care - Historial y Estado") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaOrden) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Solicitud registrada",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ultimaOrden?.let { orden ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically()
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Código: ${orden.id}", style = MaterialTheme.typography.titleMedium)
                            Text("Equipo: ${orden.equipoModelo} · Serie: ${orden.numeroSerie}")
                            Text("Problema reportado: ${orden.diagnostico}")
                            Text("Estado: ${orden.estado}")
                            Text("Guarda el código para consultar o editar tu solicitud.")
                        }
                    }
                }
            } ?: run {
                Text("Registra una solicitud para ver aquí su código de seguimiento.")
            }
        }
    }
}
