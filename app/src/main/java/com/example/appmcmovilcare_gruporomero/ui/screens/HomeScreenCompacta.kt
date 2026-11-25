package com.example.appmcmovilcare_gruporomero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenCompacta(
    viewModel: UsuarioViewModel,
    onNavegarARegistro: () -> Unit,
    onNavegarAListaSolicitudes: () -> Unit,
    onNavegarAEditarSolicitud: () -> Unit
) {
    val catalogo by viewModel.catalogoUiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.cargarCatalogo() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MCmovil Care",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Servicio Técnico Profesional",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Seguimiento y Cuidado Preventivo",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gestión del ciclo de vida de equipos TI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onNavegarARegistro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = " Ingresar Nueva Solicitud",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavegarAEditarSolicitud,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = " Consultar o modificar mi problema",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            OutlinedButton(
                onClick = onNavegarAListaSolicitudes,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Acceso administrador")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            Text("Catálogo externo de equipos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Modelos de referencia obtenidos desde DummyJSON", style = MaterialTheme.typography.bodySmall)
            when {
                catalogo.cargando -> CircularProgressIndicator()
                catalogo.error != null -> Text(catalogo.error!!, color = MaterialTheme.colorScheme.error)
                catalogo.productos.isEmpty() -> Text("No hay productos disponibles en este momento")
                else -> catalogo.productos.forEach { producto ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(producto.titulo, modifier = Modifier.weight(1f))
                            Text("$${producto.precio}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}