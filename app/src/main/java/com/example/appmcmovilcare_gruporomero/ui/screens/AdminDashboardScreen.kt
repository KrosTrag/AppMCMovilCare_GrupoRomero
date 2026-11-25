package com.example.appmcmovilcare_gruporomero.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmcmovilcare_gruporomero.data.local.OrdenTrabajoEntity
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel

private val estadosOrden = listOf("Ingresado", "En Diagnóstico", "Esperando Autorización", "Reparado", "Entregado")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: UsuarioViewModel, onCerrarSesion: () -> Unit) {
    val state by viewModel.adminUiState.collectAsState()
    LaunchedEffect(state.token) {
        if (state.token != null && state.solicitudes.isEmpty()) viewModel.cargarSolicitudesAdministrador()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Solicitudes · Administrador") },
            actions = { TextButton(onClick = { viewModel.cerrarSesionAdministrador(onCerrarSesion) }) { Text("Cerrar sesión") } }
        )
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Órdenes registradas: ${state.solicitudes.size}", fontWeight = FontWeight.Bold)
                TextButton(onClick = viewModel::cargarSolicitudesAdministrador) { Text("Actualizar") }
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp)) }
            if (state.cargando && state.solicitudes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            } else if (state.solicitudes.isEmpty()) {
                Text("No hay solicitudes registradas.", modifier = Modifier.padding(20.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.solicitudes, key = { it.id }) { ticket ->
                        AdminTicketCard(
                            ticket = ticket,
                            onCambiarEstado = { viewModel.cambiarEstadoAdministrador(ticket.id, siguienteEstado(ticket.estado)) },
                            onEliminar = { viewModel.eliminarSolicitudAdministrador(ticket.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTicketCard(ticket: OrdenTrabajoEntity, onCambiarEstado: () -> Unit, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${ticket.id} · ${ticket.estado}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${ticket.clienteNombre} · ${ticket.correoCliente}")
            Text("${ticket.equipoModelo} · Serie ${ticket.numeroSerie}")
            Text("Problema: ${ticket.diagnostico}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCambiarEstado) { Text("Avanzar estado") }
                OutlinedButton(onClick = onEliminar) { Text("Eliminar") }
            }
        }
    }
}

private fun siguienteEstado(actual: String): String {
    val index = estadosOrden.indexOf(actual)
    return estadosOrden[(index + 1).coerceAtLeast(0) % estadosOrden.size]
}