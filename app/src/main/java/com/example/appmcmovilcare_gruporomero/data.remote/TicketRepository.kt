package com.example.appmcmovilcare_gruporomero.data.remote

class TicketRepository(
    private val ticketApi: TicketApiService = ApiClient.tickets,
    private val catalogApi: ExternalCatalogApi = ApiClient.catalog
) : TicketDataSource {
    override suspend fun crear(request: CreateTicketRequest) = ticketApi.crear(request)

    override suspend fun buscarParaCliente(id: String, email: String) = ticketApi.buscarParaCliente(id, email)

    override suspend fun editarProblema(id: String, email: String, diagnostico: String) =
        ticketApi.editarProblema(id, email, EditProblemRequest(diagnostico))

    override suspend fun iniciarSesion(email: String, password: String) =
        ticketApi.iniciarSesion(AdminLoginRequest(email, password))

    override suspend fun listarAdministrador(token: String) =
        ticketApi.listarAdministrador("Bearer $token").map(TicketDto::toEntity)

    override suspend fun cambiarEstado(token: String, id: String, estado: String) =
        ticketApi.cambiarEstado("Bearer $token", id, StatusRequest(estado)).toEntity()

    override suspend fun eliminar(token: String, id: String) = ticketApi.eliminar("Bearer $token", id)

    override suspend fun cerrarSesion(token: String) = ticketApi.cerrarSesion("Bearer $token")

    override suspend fun cargarProductos() = catalogApi.smartphones().products
}