package com.example.appmcmovilcare_gruporomero.data.remote

class TicketRepository(
    private val ticketApi: TicketApiService = ApiClient.tickets,
    private val catalogApi: ExternalCatalogApi = ApiClient.catalog
) {
    suspend fun crear(request: CreateTicketRequest) = ticketApi.crear(request)

    suspend fun buscarParaCliente(id: String, email: String) = ticketApi.buscarParaCliente(id, email)

    suspend fun editarProblema(id: String, email: String, diagnostico: String) =
        ticketApi.editarProblema(id, email, EditProblemRequest(diagnostico))

    suspend fun iniciarSesion(email: String, password: String) =
        ticketApi.iniciarSesion(AdminLoginRequest(email, password))

    suspend fun listarAdministrador(token: String) =
        ticketApi.listarAdministrador("Bearer $token").map(TicketDto::toEntity)

    suspend fun cambiarEstado(token: String, id: String, estado: String) =
        ticketApi.cambiarEstado("Bearer $token", id, StatusRequest(estado)).toEntity()

    suspend fun eliminar(token: String, id: String) = ticketApi.eliminar("Bearer $token", id)

    suspend fun cerrarSesion(token: String) = ticketApi.cerrarSesion("Bearer $token")

    suspend fun cargarProductos() = catalogApi.smartphones().products
}