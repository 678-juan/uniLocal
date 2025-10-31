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
    
    // quemo moderadores aun nos e si meterlos a frebase
    private fun obtenerModeradoresQuemados(): List<Moderador> {
        return listOf(
            Moderador(
                id = "mod1",
                nombre = "Juan",
                username = "juan123",
                email = "juan-moderador@gmail.com",
                clave = "clave123",
                historial = emptyList(),
                lugares = emptyList()
            ),
            Moderador(
                id = "mod2",
                nombre = "María",
                username = "maria456",
                email = "maria-moderador@gmail.com",
                clave = "clave456",
                historial = emptyList(),
                lugares = emptyList()
            ),
            Moderador(
                id = "mod3",
                nombre = "Pedro",
                username = "pedro789",
                email = "pedro-moderador@gmail.com",
                clave = "clave789",
                historial = emptyList(),
                lugares = emptyList()
            ),
            // Moderador admin del README
            Moderador(
                id = "mod_admin",
                nombre = "Admin",
                username = "admin",
                email = "admin@unilocal.com",
                clave = "123",
                historial = emptyList(),
                lugares = emptyList()
            )
        )
    }

    // cargar moderadores (quemados + Firebase)
    fun cargarModeradores(){
        // Primero cargar moderadores quemados
        val moderadoresQuemados = obtenerModeradoresQuemados()
        _moderador.value = moderadoresQuemados
        
        // Luego cargar desde Firebase y combinar (evitando duplicados)
        viewModelScope.launch {
            try {
                // Primero, asegurar que los moderadores quemados existan en Firebase
                crearModeradoresQuemadosEnFirebase(moderadoresQuemados)
                
                // Cargar todos los moderadores desde Firebase
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
                
                // Combinar: mantener quemados y agregar los de Firebase que no existan ya
                val moderadoresCombinados = moderadoresQuemados.toMutableList()
                moderadoresFirebase.forEach { moderadorFirebase ->
                    // Solo agregar si no existe ya (por ID o email)
                    if (!moderadoresCombinados.any { 
                        it.id == moderadorFirebase.id || it.email == moderadorFirebase.email 
                    }) {
                        moderadoresCombinados.add(moderadorFirebase)
                    }
                }
                
                _moderador.value = moderadoresCombinados
            } catch (e: Exception) {
                // Si hay error con Firebase, mantener solo los quemados
                // Ya están cargados arriba
            }
        }
    }
    
    // Crear moderadores quemados en Firebase si no existen
    private suspend fun crearModeradoresQuemadosEnFirebase(moderadoresQuemados: List<Moderador>) {
        moderadoresQuemados.forEach { moderadorQuemado ->
            try {
                // Verificar si ya existe en Firebase por email
                val querySnapshot = db.collection("Moderadores")
                    .whereEqualTo("email", moderadorQuemado.email)
                    .get()
                    .await()
                
                if (querySnapshot.isEmpty) {
                    // No existe, crearlo con el ID fijo (sin historial y lugares, se manejan en subcolecciones)
                    val moderadorData = hashMapOf<String, Any>(
                        "nombre" to moderadorQuemado.nombre,
                        "username" to moderadorQuemado.username,
                        "email" to moderadorQuemado.email,
                        "clave" to moderadorQuemado.clave
                        // historial y lugares NO se guardan aquí, se manejan en subcolecciones
                    )
                    db.collection("Moderadores")
                        .document(moderadorQuemado.id)
                        .set(moderadorData)
                        .await()
                } else {
                    // Ya existe, verificar si tiene el mismo ID
                    val existente = querySnapshot.documents.firstOrNull()
                    if (existente != null && existente.id != moderadorQuemado.id) {
                        // Existe pero con otro ID, actualizar el documento con el ID correcto
                        val moderadorData = hashMapOf<String, Any>(
                            "nombre" to moderadorQuemado.nombre,
                            "username" to moderadorQuemado.username,
                            "email" to moderadorQuemado.email,
                            "clave" to moderadorQuemado.clave
                        )
                        // Crear con el ID correcto
                        db.collection("Moderadores")
                            .document(moderadorQuemado.id)
                            .set(moderadorData)
                            .await()
                        // Eliminar el duplicado si es diferente
                        if (existente.id != moderadorQuemado.id) {
                            existente.reference.delete().await()
                        }
                    }
                }
            } catch (e: Exception) {
                // Error al crear/verificar moderador quemado, continuar con el siguiente
            }
        }
    }
    
    fun crearModerador(moderador: Moderador){
        viewModelScope.launch {
            try {
                // Guardar moderador sin historial y lugares (se manejan en subcolecciones)
                val moderadorData = hashMapOf<String, Any>(
                    "nombre" to moderador.nombre,
                    "username" to moderador.username,
                    "email" to moderador.email,
                    "clave" to moderador.clave
                    // historial y lugares NO se guardan aquí, se manejan en subcolecciones
                )
                
                val docRef = db.collection("Moderadores")
                    .add(moderadorData)
                    .await()
                
                val moderadorConId = moderador.copy(id = docRef.id, historial = emptyList(), lugares = emptyList())
                _moderador.value = _moderador.value + moderadorConId
            } catch (e: Exception) {
                // Error al crear moderador
            }
        }
    }
    
    fun buscarId(id: String): Moderador? {
        return _moderador.value.find { it.id == id }
    }

    fun buscarEmail(email: String): Moderador? {
        return _moderador.value.find { it.email == email }
    }

    fun login(email: String, password: String): Moderador? {
        // Primero buscar en moderadores quemados (tienen prioridad)
        val moderadoresQuemados = obtenerModeradoresQuemados()
        val encontradoQuemado = moderadoresQuemados.find { 
            it.email == email && it.clave == password 
        }
        if (encontradoQuemado != null) {
            _moderadorActual.value = encontradoQuemado
            // Para moderadores quemados, intentar cargar datos de Firebase si existen
            cargarDatosModerador(encontradoQuemado.id)
            return encontradoQuemado
        }
        
        // Luego buscar en la lista local (que incluye quemados + Firebase)
        val encontrado = _moderador.value.find { 
            it.email == email && it.clave == password && it.id != encontradoQuemado?.id
        }
        if (encontrado != null) {
            _moderadorActual.value = encontrado
            cargarDatosModerador(encontrado.id)
            return encontrado
        }
        
        // Si no está en local, buscar en Firebase
        viewModelScope.launch {
            try {
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
                        historial = emptyList(), // Se carga desde subcolección
                        lugares = emptyList() // Se carga desde subcolección
                    )
                    
                    _moderadorActual.value = moderador
                    // Actualizar lista local (evitando duplicados)
                    if (!_moderador.value.any { it.id == moderador.id || it.email == moderador.email }) {
                        _moderador.value = _moderador.value + moderador
                    }
                    cargarDatosModerador(moderador.id)
                }
            } catch (e: Exception) {
                // Error al hacer login
            }
        }
        
        return null // Retornará null mientras se busca en Firebase
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
        val moderador = _moderadorActual.value
        if (moderador != null) {
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
                    val moderadorActualizado = moderador.copy(
                        historial = nuevoHistorial,
                        lugares = _lugaresAutorizados.value
                    )
                    _moderadorActual.value = moderadorActualizado
                    
                    // actualizar en la lista de moderadores
                    _moderador.value = _moderador.value.map { 
                        if (it.id == moderador.id) moderadorActualizado else it 
                    }
                    
                    // actualizar estado del lugar en LugaresViewModel
                    lugaresViewModel?.actualizarEstado(lugar.id, nuevaDecision)
                } catch (e: Exception) {
                    // Error al registrar decisión
                }
            }
        }
    }
}