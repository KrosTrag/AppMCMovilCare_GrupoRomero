package com.example.appmcmovilcare_gruporomero.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarSolicitudScreen(viewModel: UsuarioViewModel, onVolver: () -> Unit) {
    val state by viewModel.edicionUiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Consultar / editar problema") },
            navigationIcon = { TextButton(onClick = onVolver) { Text("← Volver") } }
        )
    }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Para proteger tu solicitud, ingresa su número y el correo usado al registrarla.")
            OutlinedTextField(
                value = state.id,
                onValueChange = viewModel::onEditIdChanged,
                label = { Text("Número de solicitud (OT-…)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.correo,
                onValueChange = viewModel::onEditEmailChanged,
                label = { Text("Correo registrado") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = viewModel::buscarSolicitudCliente,
                enabled = !state.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.cargando && !state.solicitudEncontrada) CircularProgressIndicator()
                else Text("Buscar solicitud")
            }
            if (state.solicitudEncontrada) {
                Text("Solicitud verificada. Puedes modificar la descripción del problema.")
                OutlinedTextField(
                    value = state.problema,
                    onValueChange = viewModel::onEditProblemChanged,
                    label = { Text("Descripción del problema") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = viewModel::guardarEdicionCliente,
                    enabled = !state.cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.cargando) CircularProgressIndicator()
                    else Text("Guardar cambios")
                }
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            state.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}