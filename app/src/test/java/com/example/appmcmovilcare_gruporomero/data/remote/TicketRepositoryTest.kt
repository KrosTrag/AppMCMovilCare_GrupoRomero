package com.example.appmcmovilcare_gruporomero.data.remote

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketRepositoryTest {
    private val api = FakeTicketApiService()
    private val catalog = FakeExternalCatalogApi()
    private val repository = TicketRepository(api, catalog)

    @Test
    fun crearEnviaLaSolicitudYDevuelveLaOrden() = runBlocking {
        val request = CreateTicketRequest("Ana Pérez", "ana@example.com", "Laptop", "SN-10", "No enciende")

        val result = repository.crear(request)

        assertEquals(request, api.createRequest)
        assertEquals(api.ticket, result)
    }

    @Test
    fun buscarYEditarUsanElIdentificadorCorreoYProblemaDelCliente() = runBlocking {
        val found = repository.buscarParaCliente("OT-10", "ana@example.com")
        val updated = repository.editarProblema("OT-10", "ana@example.com", "Se apaga al iniciar")

        assertEquals("OT-10", api.searchedId)
        assertEquals("ana@example.com", api.searchedEmail)
        assertEquals(api.ticket, found)
        assertEquals("OT-10", api.editedId)
        assertEquals("ana@example.com", api.editedEmail)
        assertEquals(EditProblemRequest("Se apaga al iniciar"), api.editRequest)
        assertEquals(api.ticket, updated)
    }

    @Test
    fun administraSesionEstadosYOrdenesConTokenBearer() = runBlocking {
        val login = repository.iniciarSesion("admin@example.com", "secreto")
        val tickets = repository.listarAdministrador("token-123")
        val changed = repository.cambiarEstado("token-123", "OT-10", "Reparado")
        repository.eliminar("token-123", "OT-10")
        repository.cerrarSesion("token-123")

        assertEquals(AdminLoginRequest("admin@example.com", "secreto"), api.loginRequest)
        assertEquals(AdminLoginResponse("token-123", "admin@example.com"), login)
        assertEquals("Bearer token-123", api.listAuthorization)
        assertEquals(listOf(api.ticket.toEntity()), tickets)
        assertEquals("Bearer token-123", api.statusAuthorization)
        assertEquals("OT-10", api.statusId)
        assertEquals(StatusRequest("Reparado"), api.statusRequest)
        assertEquals(api.ticket.toEntity(), changed)
        assertEquals("Bearer token-123", api.deleteAuthorization)
        assertEquals("OT-10", api.deletedId)
        assertEquals("Bearer token-123", api.logoutAuthorization)
    }

    @Test
    fun consultaCatalogoExterno() = runBlocking {
        val products = repository.cargarProductos()

        assertEquals(5, catalog.limit)
        assertEquals(listOf(ProductDto(1, "Teléfono", 499.99)), products)
    }

    @Test
    fun convierteTicketRemotoEnOrdenLocalSinPerderSusDatos() {
        val entity = api.ticket.toEntity()

        assertEquals(api.ticket.id, entity.id)
        assertEquals(api.ticket.clienteNombre, entity.clienteNombre)
        assertEquals(api.ticket.correoCliente, entity.correoCliente)
        assertEquals(api.ticket.equipoModelo, entity.equipoModelo)
        assertEquals(api.ticket.numeroSerie, entity.numeroSerie)
        assertEquals(api.ticket.diagnostico, entity.diagnostico)
        assertEquals(api.ticket.estado, entity.estado)
        assertTrue(entity.requiereAutorizacion)
        assertNull(entity.imagenEvidenciaUri)
    }
}

private class FakeTicketApiService : TicketApiService {
    val ticket = TicketDto(
        id = "OT-10",
        clienteNombre = "Ana Pérez",
        correoCliente = "ana@example.com",
        equipoModelo = "Laptop",
        numeroSerie = "SN-10",
        diagnostico = "No enciende",
        estado = "Ingresado",
        requiereAutorizacion = true
    )
    var createRequest: CreateTicketRequest? = null
    var searchedId: String? = null
    var searchedEmail: String? = null
    var editedId: String? = null
    var editedEmail: String? = null
    var editRequest: EditProblemRequest? = null
    var loginRequest: AdminLoginRequest? = null
    var listAuthorization: String? = null
    var statusAuthorization: String? = null
    var statusId: String? = null
    var statusRequest: StatusRequest? = null
    var deleteAuthorization: String? = null
    var deletedId: String? = null
    var logoutAuthorization: String? = null

    override suspend fun crear(request: CreateTicketRequest): TicketDto {
        createRequest = request
        return ticket
    }

    override suspend fun buscarParaCliente(id: String, email: String): TicketDto {
        searchedId = id
        searchedEmail = email
        return ticket
    }

    override suspend fun editarProblema(id: String, email: String, request: EditProblemRequest): TicketDto {
        editedId = id
        editedEmail = email
        editRequest = request
        return ticket
    }

    override suspend fun iniciarSesion(request: AdminLoginRequest): AdminLoginResponse {
        loginRequest = request
        return AdminLoginResponse("token-123", request.email)
    }

    override suspend fun cerrarSesion(authorization: String) {
        logoutAuthorization = authorization
    }

    override suspend fun listarAdministrador(authorization: String): List<TicketDto> {
        listAuthorization = authorization
        return listOf(ticket)
    }

    override suspend fun cambiarEstado(
        authorization: String,
        id: String,
        request: StatusRequest
    ): TicketDto {
        statusAuthorization = authorization
        statusId = id
        statusRequest = request
        return ticket
    }

    override suspend fun eliminar(authorization: String, id: String) {
        deleteAuthorization = authorization
        deletedId = id
    }
}

private class FakeExternalCatalogApi : ExternalCatalogApi {
    var limit: Int? = null

    override suspend fun smartphones(limit: Int): ProductResponse {
        this.limit = limit
        return ProductResponse(listOf(ProductDto(1, "Teléfono", 499.99)))
    }
}