package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.Moderador
import com.example.unilocal.model.entidad.Solicitud
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.unilocal.utils.RequestResult

class ModeradorViewModel : ViewModel(){

    // moderadores
    private val _moderador = MutableStateFlow(emptyList<Moderador>())
    val moderador: StateFlow<List<Moderador>> = _moderador.asStateFlow()

    private val _moderadorActual = MutableStateFlow<Moderador?>(null)
    val moderadorActual: StateFlow<Moderador?> = _moderadorActual.asStateFlow()

    // Historial cargado desde Firebase
    private val _historial = MutableStateFlow<List<Solicitud>>(emptyList())
    val historial: StateFlow<List<Solicitud>> = _historial.asStateFlow()

    // Lugares autorizados - IDs del moderador actual
    private val _lugaresAutorizados = MutableStateFlow<List<String>>(emptyList())

    // necesitamos acceso a los lugares para obtener los objetos completos
    private val _lugares = MutableStateFlow<List<Lugar>>(emptyList())
    val misAutorizados: StateFlow<List<Lugar>> = combine(_lugaresAutorizados, _lugares) { lugaresIds, lugares ->
        lugares.filter { lugar ->
            lugaresIds.contains(lugar.id)
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val db = Firebase.firestore

    init{
        cargarModeradores()
    }
    // cargar moderadores desde Firebase
    fun cargarModeradores(){
        viewModelScope.launch {
            try {
                val snapshot = db.collection("Moderadores").get().await()
                val moderadoresFirebase = snapshot.documents.mapNotNull { doc ->
                    if (doc.exists()) {
                        val data = doc.data!!
                        Moderador(
                            id = doc.id,
                            nombre = data["nombre"] as? String ?: "",
                            username = data["username"] as? String ?: "",
                            email = data["email"] as? String ?: "",
                            clave = data["clave"] as? String ?: "",
                            historial = emptyList(), // Se carga desde subcolección
                            lugares = emptyList() // Se carga desde subcolección
                        )
                    } else {
                        null
                    }
                }

                _moderador.value = moderadoresFirebase
            } catch (e: Exception) {
                // Error al cargar moderadores desde Firebase
                _moderador.value = emptyList()
            }
        }
    }

    // Crear moderadores quemados en Firebase si no existen


    fun buscarId(id: String): Moderador? {
        return _moderador.value.find { it.id == id }
    }

    fun buscarEmail(email: String): Moderador? {
        return _moderador.value.find { it.email == email }
    }

    // Resultado del login de moderador (similar a UsuarioViewModel)
    private val _moderadorResult = MutableStateFlow<RequestResult?>(null)
    val moderadorResult: StateFlow<RequestResult?> = _moderadorResult.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _moderadorResult.value = RequestResult.Cargar
            _moderadorResult.value = runCatching { loginFireBase(email, password) }.fold(
                onSuccess = { RequestResult.Sucess("Moderador autenticado") },
                onFailure = { RequestResult.Error(it.message ?: "Error iniciando sesión moderador") }
            )
        }
    }

    private suspend fun loginFireBase(email: String, password: String) {
        val snapshot = db.collection("Moderadores")
            .whereEqualTo("email", email)
            .whereEqualTo("clave", password)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull()
        if (doc != null && doc.exists()) {
            val data = doc.data!!
            val moderador = Moderador(
                id = doc.id,
                nombre = data["nombre"] as? String ?: "",
                username = data["username"] as? String ?: "",
                email = data["email"] as? String ?: "",
                clave = data["clave"] as? String ?: "",
                historial = emptyList(),
                lugares = emptyList()
            )

            _moderadorActual.value = moderador

            // Actualizar lista local (evitando duplicados)
            if (!_moderador.value.any { it.id == moderador.id || it.email == moderador.email }) {
                _moderador.value = _moderador.value + moderador
            }

            cargarDatosModerador(moderador.id)
        } else {
            throw Exception("Moderador no encontrado")
        }
    }

    private fun cargarDatosModerador(moderadorId: String) {
        viewModelScope.launch {
            try {
                // Cargar historial
                val historialSnapshot = db.collection("Moderadores")
                    .document(moderadorId)
                    .collection("historial")
                    .orderBy("fechaIso", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val historialList = historialSnapshot.documents.mapNotNull { doc ->
                    if (doc.exists()) {
                        val data = doc.data!!
                        val accionStr = data["accion"] as? String ?: "PENDIENTE"
                        val accion = try {
                            EstadoLugar.valueOf(accionStr)
                        } catch (e: Exception) {
                            EstadoLugar.PENDIENTE
                        }

                        Solicitud(
                            lugarId = data["lugarId"] as? String ?: "",
                            lugarNombre = data["lugarNombre"] as? String ?: "",
                            moderadorId = data["moderadorId"] as? String ?: "",
                            accion = accion,
                            motivo = data["motivo"] as? String ?: "",
                            fechaIso = data["fechaIso"] as? String ?: ""
                        )
                    } else {
                        null
                    }
                }
                _historial.value = historialList

                // Cargar lugares autorizados (IDs)
                val lugaresSnapshot = db.collection("Moderadores")
                    .document(moderadorId)
                    .collection("lugaresAutorizados")
                    .get()
                    .await()

                val lugaresIds = lugaresSnapshot.documents.map { it.id }
                _lugaresAutorizados.value = lugaresIds

                // Actualizar moderador actual con datos de Firebase
                val moderadorActual = _moderadorActual.value
                if (moderadorActual != null) {
                    _moderadorActual.value = moderadorActual.copy(
                        historial = historialList,
                        lugares = lugaresIds
                    )
                }
            } catch (e: Exception) {
                // Error al cargar datos
            }
        }
    }

    fun cerrarSesion() {
        _moderadorActual.value = null
    }

    // actualizar lugares (necesario para misAutorizados)
    fun actualizarLugares(lugares: List<Lugar>) {
        _lugares.value = lugares
    }

    // moderar lugares
    fun registrarDecision(
        lugar: Lugar,
        moderadorId: String,
        nuevaDecision: EstadoLugar,
        motivo: String = "",
        lugaresViewModel: LugaresViewModel? = null
    ) {
        // Siempre intentar registrar la decisión en Firebase con el moderadorId proporcionado.
        viewModelScope.launch {
            try {
                val solicitud = Solicitud(
                    lugarId = lugar.id,
                    lugarNombre = lugar.nombre,
                    moderadorId = moderadorId,
                    accion = nuevaDecision,
                    motivo = motivo,
                    fechaIso = System.currentTimeMillis().toString()
                )

                // Guardar solicitud en Firebase (subcolección, convertir enum a String)
                val solicitudData = hashMapOf<String, Any>(
                    "lugarId" to solicitud.lugarId,
                    "lugarNombre" to solicitud.lugarNombre,
                    "moderadorId" to solicitud.moderadorId,
                    "accion" to solicitud.accion.name, // Guardar enum como String
                    "motivo" to solicitud.motivo,
                    "fechaIso" to solicitud.fechaIso
                )

                db.collection("Moderadores")
                    .document(moderadorId)
                    .collection("historial")
                    .add(solicitudData)
                    .await()

                // Si el moderador actual en memoria coincide con el moderadorId, actualizar el estado local
                val moderadorActualEnMemoria = _moderadorActual.value
                if (moderadorActualEnMemoria != null && moderadorActualEnMemoria.id == moderadorId) {
                    // Actualizar historial local
                    val nuevoHistorial = _historial.value + solicitud
                    _historial.value = nuevoHistorial

                    // Actualizar lugares autorizados
                    if (nuevaDecision == EstadoLugar.AUTORIZADO) {
                        if (!_lugaresAutorizados.value.contains(lugar.id)) {
                            // Agregar a Firebase
                            db.collection("Moderadores")
                                .document(moderadorId)
                                .collection("lugaresAutorizados")
                                .document(lugar.id)
                                .set(mapOf("lugarId" to lugar.id, "fecha" to System.currentTimeMillis()))
                                .await()

                            // Actualizar local
                            _lugaresAutorizados.value = _lugaresAutorizados.value + lugar.id
                        }
                    } else if (nuevaDecision == EstadoLugar.RECHAZADO) {
                        // Eliminar de Firebase
                        db.collection("Moderadores")
                            .document(moderadorId)
                            .collection("lugaresAutorizados")
                            .document(lugar.id)
                            .delete()
                            .await()

                        // Actualizar local
                        _lugaresAutorizados.value = _lugaresAutorizados.value.filter { it != lugar.id }
                    }

                    // actualizar moderador actual
                    val moderadorActualizado = moderadorActualEnMemoria.copy(
                        historial = _historial.value,
                        lugares = _lugaresAutorizados.value
                    )
                    _moderadorActual.value = moderadorActualizado

                    // actualizar en la lista de moderadores
                    _moderador.value = _moderador.value.map {
                        if (it.id == moderadorActualEnMemoria.id) moderadorActualizado else it
                    }
                }

                // actualizar estado del lugar en LugaresViewModel (independiente de si el moderadorActual estaba en memoria)
                lugaresViewModel?.actualizarEstado(lugar.id, nuevaDecision)
            } catch (e: Exception) {
                // Error al registrar decisión
            }
        }
    }
}

