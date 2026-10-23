package com.example.mcmovilcare.model

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val equipoModelo: String = "",
    val errores: UsuarioErrores = UsuarioErrores(),
    val aceptaTerminos: Boolean = false
)

data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val direccion: String? = null
)