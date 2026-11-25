package com.example.appmcmovilcare_gruporomero.model

data class FormValidation(
    val errorNombre: String?,
    val errorCorreo: String?,
    val errorModelo: String?,
    val errorSerie: String?,
    val esValido: Boolean
)

object TicketFormValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun validar(nombre: String, correo: String, modelo: String, serie: String): FormValidation {
        val errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
        val errorCorreo = when {
            correo.isBlank() -> "El correo es obligatorio"
            !emailRegex.matches(correo.trim()) -> "Formato de correo inválido"
            else -> null
        }
        val errorModelo = if (modelo.isBlank()) "El modelo es obligatorio" else null
        val errorSerie = if (serie.isBlank()) "El N° de serie es obligatorio" else null
        return FormValidation(
            errorNombre = errorNombre,
            errorCorreo = errorCorreo,
            errorModelo = errorModelo,
            errorSerie = errorSerie,
            esValido = errorNombre == null && errorCorreo == null && errorModelo == null && errorSerie == null
        )
    }
}