package com.example.appmcmovilcare_gruporomero.data.remote

import com.example.appmcmovilcare_gruporomero.data.local.OrdenTrabajoEntity

interface TicketDataSource {
    suspend fun crear(request: CreateTicketRequest): TicketDto
    suspend fun buscarParaCliente(id: String, email: String): TicketDto
    suspend fun editarProblema(id: String, email: String, diagnostico: String): TicketDto
    suspend fun iniciarSesion(email: String, password: String): AdminLoginResponse
    suspend fun listarAdministrador(token: String): List<OrdenTrabajoEntity>
    suspend fun cambiarEstado(token: String, id: String, estado: String): OrdenTrabajoEntity
    suspend fun eliminar(token: String, id: String)
    suspend fun cerrarSesion(token: String)
    suspend fun cargarProductos(): List<ProductDto>
}