package com.example.appmcmovilcare_gruporomero.data.remote

import com.example.appmcmovilcare_gruporomero.data.local.OrdenTrabajoEntity

data class TicketDto(
    val id: String,
    val clienteNombre: String,
    val correoCliente: String,
    val equipoModelo: String,
    val numeroSerie: String,
    val diagnostico: String,
    val estado: String,
    val requiereAutorizacion: Boolean,
    val creadoEn: String? = null
) {
    fun toEntity() = OrdenTrabajoEntity(
        id = id,
        clienteNombre = clienteNombre,
        correoCliente = correoCliente,
        equipoModelo = equipoModelo,
        numeroSerie = numeroSerie,
        diagnostico = diagnostico,
        estado = estado,
        requiereAutorizacion = requiereAutorizacion
    )
}

data class CreateTicketRequest(
    val clienteNombre: String,
    val correoCliente: String,
    val equipoModelo: String,
    val numeroSerie: String,
    val diagnostico: String
)

data class EditProblemRequest(val diagnostico: String)
data class StatusRequest(val estado: String)
data class AdminLoginRequest(val email: String, val password: String)
data class AdminLoginResponse(val token: String, val email: String)
data class ProductResponse(val products: List<ProductDto>)
data class ProductDto(val id: Int, val title: String, val price: Double)