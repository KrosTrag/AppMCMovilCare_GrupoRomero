package com.example.appmcmovilcare_gruporomero.model

data class RegistroUiState(
    val clienteNombre: String = "",
    val correoCliente: String = "",
    val equipoModelo: String = "",
    val numeroSerie: String = "",
    val diagnostico: String = "",
    val imagenUri: String? = null,
    // Errores de validación
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorModelo: String? = null,
    val errorSerie: String? = null,
    val errorRegistro: String? = null,
    val cargandoRegistro: Boolean = false,
    val esFormularioValido: Boolean = false
)

data class EdicionUiState(
    val id: String = "",
    val correo: String = "",
    val problema: String = "",
    val solicitudEncontrada: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

data class AdminUiState(
    val token: String? = null,
    val solicitudes: List<com.example.appmcmovilcare_gruporomero.data.local.OrdenTrabajoEntity> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

data class ProductoUi(
    val id: Int,
    val titulo: String,
    val precio: Double
)

data class CatalogoUiState(
    val productos: List<ProductoUi> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)