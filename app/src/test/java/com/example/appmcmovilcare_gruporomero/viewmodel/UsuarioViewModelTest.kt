package com.example.appmcmovilcare_gruporomero.viewmodel

import android.app.Application
import com.example.appmcmovilcare_gruporomero.data.local.OrdenDao
import com.example.appmcmovilcare_gruporomero.data.local.OrdenTrabajoEntity
import com.example.appmcmovilcare_gruporomero.data.remote.AdminLoginResponse
import com.example.appmcmovilcare_gruporomero.data.remote.CreateTicketRequest
import com.example.appmcmovilcare_gruporomero.data.remote.ProductDto
import com.example.appmcmovilcare_gruporomero.data.remote.TicketDataSource
import com.example.appmcmovilcare_gruporomero.data.remote.TicketDto
import com.example.appmcmovilcare_gruporomero.model.AdminUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun configurarDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun restaurarDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun validaFormularioYRegistraOrdenConEvidencia() = runTest(dispatcher) {
        val dao = FakeOrdenDao()
        val repository = FakeTicketDataSource()
        val viewModel = crearViewModel(dao, repository)
        var exitoso = false

        viewModel.guardarOrden { exitoso = true }
        assertEquals("Completa los datos y describe el problema del equipo", viewModel.uiState.value.errorRegistro)

        viewModel.onNombreChanged(" Ana Pérez ")
        viewModel.onCorreoChanged(" ana@example.com ")
        viewModel.onModeloChanged(" Laptop ")
        viewModel.onSerieChanged(" SN-12 ")
        assertTrue(viewModel.uiState.value.esFormularioValido)
        viewModel.guardarOrden { exitoso = true }
        assertEquals("Completa los datos y describe el problema del equipo", viewModel.uiState.value.errorRegistro)

        viewModel.onDiagnosticoChanged(" No enciende ")
        viewModel.onImagenCapturada("content://evidencia/12")
        viewModel.guardarOrden { exitoso = true }

        assertEquals(
            CreateTicketRequest("Ana Pérez", "ana@example.com", "Laptop", "SN-12", "No enciende"),
            repository.createRequest
        )
        assertEquals(repository.ticket.toEntity("content://evidencia/12"), viewModel.ultimaOrdenCreada.value)
        assertEquals(viewModel.ultimaOrdenCreada.value, dao.ordenes["OT-12"])
        assertTrue(exitoso)
        assertFalse(viewModel.uiState.value.cargandoRegistro)
        assertEquals("", viewModel.uiState.value.clienteNombre)

        viewModel.onImagenCapturada("content://evidencia/13")
        viewModel.onImagenEliminada()
        assertNull(viewModel.uiState.value.imagenUri)
    }

    @Test
    fun muestraErroresDeRegistroSegunLaRespuestaDelServidor() = runTest(dispatcher) {
        val repository = FakeTicketDataSource().apply { createError = IOException("offline") }
        val viewModel = crearViewModel(FakeOrdenDao(), repository)
        completarFormulario(viewModel)

        viewModel.guardarOrden { }

        assertEquals(
            "No se pudo conectar con el servidor. Comprueba que el backend esté ejecutándose.",
            viewModel.uiState.value.errorRegistro
        )
        repository.createError = httpError(500)
        viewModel.guardarOrden { }
        assertEquals("El servidor respondió con error (500).", viewModel.uiState.value.errorRegistro)
    }

    @Test
    fun buscaYActualizaProblemaDelCliente() = runTest(dispatcher) {
        val dao = FakeOrdenDao()
        val repository = FakeTicketDataSource()
        val viewModel = crearViewModel(dao, repository)

        viewModel.buscarSolicitudCliente()
        assertEquals("Ingresa el número de solicitud y el correo registrado", viewModel.edicionUiState.value.error)

        viewModel.onEditIdChanged(" OT-12 ")
        viewModel.onEditEmailChanged(" ana@example.com ")
        viewModel.buscarSolicitudCliente()
        assertTrue(viewModel.edicionUiState.value.solicitudEncontrada)
        assertEquals("OT-12", repository.searchedId)
        assertEquals("ana@example.com", repository.searchedEmail)
        assertEquals("No enciende", viewModel.edicionUiState.value.problema)

        viewModel.onEditProblemChanged(" ")
        viewModel.guardarEdicionCliente()
        assertEquals("El problema no puede quedar vacío", viewModel.edicionUiState.value.error)

        viewModel.onEditProblemChanged(" Se apaga al iniciar ")
        viewModel.guardarEdicionCliente()
        assertEquals("Se apaga al iniciar", repository.editedProblem)
        assertEquals("Se apaga al iniciar", viewModel.edicionUiState.value.problema)
        assertEquals("Problema actualizado", viewModel.edicionUiState.value.mensaje)
        assertEquals("Se apaga al iniciar", dao.ordenes["OT-12"]?.diagnostico)
    }

    @Test
    fun informaErroresDePropiedadYSolicitudNoEncontrada() = runTest(dispatcher) {
        val repository = FakeTicketDataSource().apply { searchError = httpError(403) }
        val viewModel = crearViewModel(FakeOrdenDao(), repository)
        viewModel.onEditIdChanged("OT-12")
        viewModel.onEditEmailChanged("otro@example.com")

        viewModel.buscarSolicitudCliente()
        assertEquals("Los datos no corresponden a esta solicitud.", viewModel.edicionUiState.value.error)

        repository.searchError = null
        viewModel.buscarSolicitudCliente()
        repository.editError = httpError(404)
        viewModel.onEditProblemChanged("Problema actualizado")
        viewModel.guardarEdicionCliente()
        assertEquals("No se encontró la solicitud indicada.", viewModel.edicionUiState.value.error)
    }

    @Test
    fun cargaCatalogoExternoUnaVezYPresentaEstadoDeError() = runTest(dispatcher) {
        val repository = FakeTicketDataSource()
        val viewModel = crearViewModel(FakeOrdenDao(), repository)

        viewModel.cargarCatalogo()
        viewModel.cargarCatalogo()

        assertEquals(1, repository.catalogRequests)
        assertEquals("Teléfono", viewModel.catalogoUiState.value.productos.single().titulo)
        assertEquals(499.99, viewModel.catalogoUiState.value.productos.single().precio, 0.0)
        assertFalse(viewModel.catalogoUiState.value.cargando)

        val failedViewModel = crearViewModel(
            FakeOrdenDao(),
            FakeTicketDataSource().apply { catalogError = IOException("offline") }
        )
        failedViewModel.cargarCatalogo()
        assertEquals("No se pudo cargar el catálogo externo", failedViewModel.catalogoUiState.value.error)
        assertFalse(failedViewModel.catalogoUiState.value.cargando)
    }

    @Test
    fun administraSolicitudesYFinalizaLaSesion() = runTest(dispatcher) {
        val dao = FakeOrdenDao()
        val repository = FakeTicketDataSource()
        val viewModel = crearViewModel(dao, repository)
        var loginExitoso = false
        var sesionCerrada = false

        viewModel.iniciarSesionAdministrador(" admin@example.com ", "secreto") { loginExitoso = true }
        assertTrue(loginExitoso)
        assertEquals("admin@example.com", repository.loginEmail)
        assertEquals("admin-token", viewModel.adminUiState.value.token)

        viewModel.cargarSolicitudesAdministrador()
        assertEquals(listOf(repository.ticket.toEntity()), viewModel.adminUiState.value.solicitudes)
        viewModel.cambiarEstadoAdministrador("OT-12", "Reparado")
        assertEquals("Reparado", viewModel.adminUiState.value.solicitudes.single().estado)
        viewModel.eliminarSolicitudAdministrador("OT-12")
        assertTrue(viewModel.adminUiState.value.solicitudes.isEmpty())
        assertFalse(dao.ordenes.containsKey("OT-12"))

        viewModel.cerrarSesionAdministrador { sesionCerrada = true }
        assertTrue(sesionCerrada)
        assertEquals("admin-token", repository.closedToken)
        assertEquals(AdminUiState(), viewModel.adminUiState.value)
    }

    @Test
    fun manejaErroresAdministrativosYOperacionesSinSesion() = runTest(dispatcher) {
        val repository = FakeTicketDataSource().apply { loginError = httpError(401) }
        val viewModel = crearViewModel(FakeOrdenDao(), repository)
        var cerrada = false

        viewModel.cargarSolicitudesAdministrador()
        viewModel.cambiarEstadoAdministrador("OT-12", "Reparado")
        viewModel.eliminarSolicitudAdministrador("OT-12")
        viewModel.cerrarSesionAdministrador { cerrada = true }
        assertTrue(cerrada)
        assertNull(repository.closedToken)

        viewModel.iniciarSesionAdministrador("admin@example.com", "mala") { }
        assertEquals(
            "Acceso no autorizado. Revisa las credenciales o inicia sesión nuevamente.",
            viewModel.adminUiState.value.error
        )

        repository.loginError = null
        viewModel.iniciarSesionAdministrador("admin@example.com", "secreto") { }
        repository.listError = IOException("offline")
        viewModel.cargarSolicitudesAdministrador()
        assertEquals(
            "No se pudo conectar con el servidor. Comprueba que el backend esté ejecutándose.",
            viewModel.adminUiState.value.error
        )

        repository.statusError = httpError(500)
        viewModel.cambiarEstadoAdministrador("OT-12", "Reparado")
        assertEquals("El servidor respondió con error (500).", viewModel.adminUiState.value.error)

        repository.deleteError = RuntimeException()
        viewModel.eliminarSolicitudAdministrador("OT-12")
        assertEquals("Ocurrió un error inesperado.", viewModel.adminUiState.value.error)

        repository.logoutError = IllegalStateException("sesión vencida")
        viewModel.cerrarSesionAdministrador { cerrada = true }
        assertEquals(AdminUiState(), viewModel.adminUiState.value)
        assertEquals("sesión vencida", repository.logoutError?.localizedMessage)
    }

    private fun crearViewModel(dao: FakeOrdenDao, repository: FakeTicketDataSource) =
        UsuarioViewModel(mock(Application::class.java), dao, repository)

    private fun completarFormulario(viewModel: UsuarioViewModel) {
        viewModel.onNombreChanged("Ana Pérez")
        viewModel.onCorreoChanged("ana@example.com")
        viewModel.onModeloChanged("Laptop")
        viewModel.onSerieChanged("SN-12")
        viewModel.onDiagnosticoChanged("No enciende")
    }

    private fun httpError(code: Int) = HttpException(
        Response.error<Any>(code, ByteArray(0).toResponseBody())
    )
}

private class FakeOrdenDao : OrdenDao {
    val ordenes = linkedMapOf<String, OrdenTrabajoEntity>()
    private val ordenesState = MutableStateFlow(emptyList<OrdenTrabajoEntity>())

    override fun obtenerTodasLasOrdenes(): Flow<List<OrdenTrabajoEntity>> = ordenesState

    override suspend fun insertarOrden(orden: OrdenTrabajoEntity) {
        ordenes[orden.id] = orden
        publicar()
    }

    override suspend fun actualizarOrden(orden: OrdenTrabajoEntity) {
        ordenes[orden.id] = orden
        publicar()
    }

    override suspend fun eliminarOrden(orden: OrdenTrabajoEntity) {
        ordenes.remove(orden.id)
        publicar()
    }

    override suspend fun eliminarPorId(id: String) {
        ordenes.remove(id)
        publicar()
    }

    private fun publicar() {
        ordenesState.value = ordenes.values.toList()
    }
}

private class FakeTicketDataSource : TicketDataSource {
    val ticket = TicketDto("OT-12", "Ana Pérez", "ana@example.com", "Laptop", "SN-12", "No enciende", "Ingresado", true)
    var createRequest: CreateTicketRequest? = null
    var searchedId: String? = null
    var searchedEmail: String? = null
    var editedProblem: String? = null
    var loginEmail: String? = null
    var closedToken: String? = null
    var catalogRequests = 0
    var createError: Exception? = null
    var searchError: Exception? = null
    var editError: Exception? = null
    var loginError: Exception? = null
    var listError: Exception? = null
    var statusError: Exception? = null
    var deleteError: Exception? = null
    var logoutError: Exception? = null
    var catalogError: Exception? = null

    override suspend fun crear(request: CreateTicketRequest): TicketDto {
        createRequest = request
        createError?.let { throw it }
        return ticket
    }

    override suspend fun buscarParaCliente(id: String, email: String): TicketDto {
        searchedId = id
        searchedEmail = email
        searchError?.let { throw it }
        return ticket
    }

    override suspend fun editarProblema(id: String, email: String, diagnostico: String): TicketDto {
        editedProblem = diagnostico
        editError?.let { throw it }
        return ticket.copy(diagnostico = diagnostico)
    }

    override suspend fun iniciarSesion(email: String, password: String): AdminLoginResponse {
        loginEmail = email
        loginError?.let { throw it }
        return AdminLoginResponse("admin-token", email)
    }

    override suspend fun listarAdministrador(token: String): List<OrdenTrabajoEntity> {
        listError?.let { throw it }
        return listOf(ticket.toEntity())
    }

    override suspend fun cambiarEstado(token: String, id: String, estado: String): OrdenTrabajoEntity {
        statusError?.let { throw it }
        return ticket.copy(estado = estado).toEntity()
    }

    override suspend fun eliminar(token: String, id: String) {
        deleteError?.let { throw it }
    }

    override suspend fun cerrarSesion(token: String) {
        closedToken = token
        logoutError?.let { throw it }
    }

    override suspend fun cargarProductos(): List<ProductDto> {
        catalogRequests++
        catalogError?.let { throw it }
        return listOf(ProductDto(1, "Teléfono", 499.99))
    }
}

private fun TicketDto.toEntity(imageUri: String? = null) = OrdenTrabajoEntity(
    id = id,
    clienteNombre = clienteNombre,
    correoCliente = correoCliente,
    equipoModelo = equipoModelo,
    numeroSerie = numeroSerie,
    diagnostico = diagnostico,
    estado = estado,
    requiereAutorizacion = requiereAutorizacion,
    imagenEvidenciaUri = imageUri
)