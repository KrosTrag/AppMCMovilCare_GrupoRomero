package com.example.appmcmovilcare_gruporomero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmcmovilcare_gruporomero.data.local.AppDatabase
import com.example.appmcmovilcare_gruporomero.data.local.OrdenTrabajoEntity
import com.example.appmcmovilcare_gruporomero.data.remote.CreateTicketRequest
import com.example.appmcmovilcare_gruporomero.data.remote.TicketRepository
import com.example.appmcmovilcare_gruporomero.model.AdminUiState
import com.example.appmcmovilcare_gruporomero.model.CatalogoUiState
import com.example.appmcmovilcare_gruporomero.model.EdicionUiState
import com.example.appmcmovilcare_gruporomero.model.ProductoUi
import com.example.appmcmovilcare_gruporomero.model.RegistroUiState
import com.example.appmcmovilcare_gruporomero.model.TicketFormValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val ordenDao = AppDatabase.getDatabase(application).ordenDao()
    private val repository = TicketRepository()

    val ordenesLocales: StateFlow<List<OrdenTrabajoEntity>> =
        ordenDao.obtenerTodasLasOrdenes().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    private val _ultimaOrdenCreada = MutableStateFlow<OrdenTrabajoEntity?>(null)
    val ultimaOrdenCreada: StateFlow<OrdenTrabajoEntity?> = _ultimaOrdenCreada.asStateFlow()

    private val _edicionUiState = MutableStateFlow(EdicionUiState())
    val edicionUiState: StateFlow<EdicionUiState> = _edicionUiState.asStateFlow()

    private val _adminUiState = MutableStateFlow(AdminUiState())
    val adminUiState: StateFlow<AdminUiState> = _adminUiState.asStateFlow()

    private val _catalogoUiState = MutableStateFlow(CatalogoUiState())
    val catalogoUiState: StateFlow<CatalogoUiState> = _catalogoUiState.asStateFlow()

    fun cargarCatalogo() {
        if (_catalogoUiState.value.cargando || _catalogoUiState.value.productos.isNotEmpty()) return
        viewModelScope.launch {
            _catalogoUiState.update { it.copy(cargando = true, error = null) }
            try {
                val productos = repository.cargarProductos().map {
                    ProductoUi(id = it.id, titulo = it.title, precio = it.price)
                }
                _catalogoUiState.value = CatalogoUiState(productos = productos)
            } catch (error: Exception) {
                _catalogoUiState.update { it.copy(cargando = false, error = "No se pudo cargar el catálogo externo") }
            }
        }
    }

    fun onNombreChanged(nuevo: String) {
        _uiState.update { it.copy(clienteNombre = nuevo, errorRegistro = null) }
        validarFormulario()
    }

    fun onCorreoChanged(nuevo: String) {
        _uiState.update { it.copy(correoCliente = nuevo, errorRegistro = null) }
        validarFormulario()
    }

    fun onModeloChanged(nuevo: String) {
        _uiState.update { it.copy(equipoModelo = nuevo, errorRegistro = null) }
        validarFormulario()
    }

    fun onSerieChanged(nuevo: String) {
        _uiState.update { it.copy(numeroSerie = nuevo, errorRegistro = null) }
        validarFormulario()
    }

    fun onDiagnosticoChanged(nuevo: String) {
        _uiState.update { it.copy(diagnostico = nuevo, errorRegistro = null) }
    }

    fun onImagenCapturada(uri: String) {
        _uiState.update { it.copy(imagenUri = uri) }
    }

    fun onImagenEliminada() {
        _uiState.update { it.copy(imagenUri = null) }
    }

    private fun validarFormulario() {
        val state = _uiState.value
        val validation = TicketFormValidator.validar(
            state.clienteNombre, state.correoCliente, state.equipoModelo, state.numeroSerie
        )
        _uiState.update {
            it.copy(
                errorNombre = validation.errorNombre,
                errorCorreo = validation.errorCorreo,
                errorModelo = validation.errorModelo,
                errorSerie = validation.errorSerie,
                esFormularioValido = validation.esValido
            )
        }
    }

    fun guardarOrden(onExito: () -> Unit) {
        val state = _uiState.value
        if (!state.esFormularioValido || state.diagnostico.isBlank()) {
            _uiState.update { it.copy(errorRegistro = "Completa los datos y describe el problema del equipo") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoRegistro = true, errorRegistro = null) }
            try {
                val remote = repository.crear(
                    CreateTicketRequest(
                        clienteNombre = state.clienteNombre.trim(),
                        correoCliente = state.correoCliente.trim(),
                        equipoModelo = state.equipoModelo.trim(),
                        numeroSerie = state.numeroSerie.trim(),
                        diagnostico = state.diagnostico.trim()
                    )
                )
                val nuevaOrden = remote.toEntity().copy(imagenEvidenciaUri = state.imagenUri)
                ordenDao.insertarOrden(nuevaOrden)
                _ultimaOrdenCreada.value = nuevaOrden
                _uiState.value = RegistroUiState()
                onExito()
            } catch (error: Exception) {
                _uiState.update { it.copy(cargandoRegistro = false, errorRegistro = mensajeError(error)) }
            }
        }
    }

    fun onEditIdChanged(value: String) {
        _edicionUiState.update { it.copy(id = value.trim(), solicitudEncontrada = false, error = null, mensaje = null) }
    }

    fun onEditEmailChanged(value: String) {
        _edicionUiState.update { it.copy(correo = value.trim(), solicitudEncontrada = false, error = null, mensaje = null) }
    }

    fun buscarSolicitudCliente() {
        val state = _edicionUiState.value
        if (state.id.isBlank() || state.correo.isBlank()) {
            _edicionUiState.update { it.copy(error = "Ingresa el número de solicitud y el correo registrado") }
            return
        }
        viewModelScope.launch {
            _edicionUiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            try {
                val ticket = repository.buscarParaCliente(state.id, state.correo).toEntity()
                ordenDao.insertarOrden(ticket)
                _edicionUiState.update {
                    it.copy(cargando = false, problema = ticket.diagnostico, solicitudEncontrada = true)
                }
            } catch (error: Exception) {
                _edicionUiState.update { it.copy(cargando = false, error = mensajeError(error)) }
            }
        }
    }

    fun onEditProblemChanged(value: String) {
        _edicionUiState.update { it.copy(problema = value, error = null, mensaje = null) }
    }

    fun guardarEdicionCliente() {
        val state = _edicionUiState.value
        if (!state.solicitudEncontrada || state.problema.isBlank()) {
            _edicionUiState.update { it.copy(error = "El problema no puede quedar vacío") }
            return
        }
        viewModelScope.launch {
            _edicionUiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            try {
                val ticket = repository.editarProblema(state.id, state.correo, state.problema.trim()).toEntity()
                ordenDao.insertarOrden(ticket)
                _edicionUiState.update { it.copy(cargando = false, problema = ticket.diagnostico, mensaje = "Problema actualizado") }
            } catch (error: Exception) {
                _edicionUiState.update { it.copy(cargando = false, error = mensajeError(error)) }
            }
        }
    }

    fun iniciarSesionAdministrador(email: String, password: String, onExito: () -> Unit) {
        viewModelScope.launch {
            _adminUiState.update { it.copy(cargando = true, error = null) }
            try {
                val response = repository.iniciarSesion(email.trim(), password)
                _adminUiState.update { it.copy(cargando = false, token = response.token, error = null) }
                onExito()
            } catch (error: Exception) {
                _adminUiState.update { it.copy(cargando = false, error = mensajeError(error)) }
            }
        }
    }

    fun cargarSolicitudesAdministrador() {
        val token = _adminUiState.value.token ?: return
        viewModelScope.launch {
            _adminUiState.update { it.copy(cargando = true, error = null) }
            try {
                val tickets = repository.listarAdministrador(token)
                _adminUiState.update { it.copy(cargando = false, solicitudes = tickets) }
            } catch (error: Exception) {
                _adminUiState.update { it.copy(cargando = false, error = mensajeError(error)) }
            }
        }
    }

    fun cambiarEstadoAdministrador(id: String, estado: String) {
        val token = _adminUiState.value.token ?: return
        viewModelScope.launch {
            try {
                val updated = repository.cambiarEstado(token, id, estado)
                _adminUiState.update { state ->
                    state.copy(solicitudes = state.solicitudes.map { if (it.id == id) updated else it }, error = null)
                }
            } catch (error: Exception) {
                _adminUiState.update { it.copy(error = mensajeError(error)) }
            }
        }
    }

    fun eliminarSolicitudAdministrador(id: String) {
        val token = _adminUiState.value.token ?: return
        viewModelScope.launch {
            try {
                repository.eliminar(token, id)
                ordenDao.eliminarPorId(id)
                _adminUiState.update { state -> state.copy(solicitudes = state.solicitudes.filterNot { it.id == id }) }
            } catch (error: Exception) {
                _adminUiState.update { it.copy(error = mensajeError(error)) }
            }
        }
    }

    fun cerrarSesionAdministrador(onCerrada: () -> Unit) {
        val token = _adminUiState.value.token
        viewModelScope.launch {
            if (token != null) {
                runCatching { repository.cerrarSesion(token) }
            }
            _adminUiState.value = AdminUiState()
            onCerrada()
        }
    }

    private fun mensajeError(error: Exception): String = when (error) {
        is HttpException -> when (error.code()) {
            401 -> "Acceso no autorizado. Revisa las credenciales o inicia sesión nuevamente."
            403 -> "Los datos no corresponden a esta solicitud."
            404 -> "No se encontró la solicitud indicada."
            else -> "El servidor respondió con error (${error.code()})."
        }
        is IOException -> "No se pudo conectar con el servidor. Comprueba que el backend esté ejecutándose."
        else -> error.localizedMessage ?: "Ocurrió un error inesperado."
    }
}