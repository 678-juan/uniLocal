package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.Moderador
import com.example.unilocal.model.entidad.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ModeradorViewModel : ViewModel(){

    // moderadores
    private val _moderador = MutableStateFlow(emptyList<Moderador>())
    val moderador: StateFlow<List<Moderador>> = _moderador.asStateFlow()

    private val _moderadorActual = MutableStateFlow<Moderador?>(null)
    val moderadorActual: StateFlow<Moderador?> = _moderadorActual.asStateFlow()

    // historial y lugares autorizados del moderador actual
    val historial: StateFlow<List<Solicitud>> = _moderadorActual.map { it?.historial ?: emptyList() }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // necesitamos acceso a los lugares para obtener los objetos completos
    private val _lugares = MutableStateFlow<List<Lugar>>(emptyList())
    val misAutorizados: StateFlow<List<Lugar>> = combine(_moderadorActual, _lugares) { moderador, lugares ->
        if (moderador != null) {
            lugares.filter { lugar -> 
                moderador.lugares.contains(lugar.id) 
            }
        } else {
            emptyList()
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init{
        cargarModeradores()
    }

    // cargar moderadores
    fun cargarModeradores(){
        val moderador1 = Moderador("1", "Juan", "juan123", "juan.moderador@gmail.com", "clave123")
        val moderador2 = Moderador("2", "María", "maria456", "maria.moderador@gmail.com", "clave456")
        val moderador3 = Moderador("3", "Pedro", "pedro789", "pedro.moderador@gmail.com", "clave789")

        _moderador.value = listOf(moderador1, moderador2, moderador3)
    }
    
    fun crearModerador(moderador: Moderador){
        _moderador.value += moderador
    }
    
    fun buscarId(id: String): Moderador? {
        return _moderador.value.find { it.id == id }
    }

    fun buscarEmail(email: String): Moderador? {
        return _moderador.value.find { it.email == email }
    }

    fun login(email: String, password: String): Moderador? {
        val encontrado = _moderador.value.find { it.email == email && it.clave == password }
        if (encontrado != null) {
            _moderadorActual.value = encontrado
        }
        return encontrado
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
        motivo: String = ""
    ) {
        val moderador = _moderadorActual.value
        if (moderador != null) {
            val solicitud = Solicitud(
                lugarId = lugar.id,
                lugarNombre = lugar.nombre,
                moderadorId = moderadorId,
                accion = nuevaDecision,
                motivo = motivo,
                fechaIso = System.currentTimeMillis().toString()
            )
            
            // actualizar historial del moderador
            val nuevoHistorial = moderador.historial + solicitud
            
            // actualizar lugares autorizados
            val nuevosLugares = if (nuevaDecision == EstadoLugar.AUTORIZADO) {
                if (!moderador.lugares.contains(lugar.id)) {
                    moderador.lugares + lugar.id
                } else {
                    moderador.lugares
                }
            } else if (nuevaDecision == EstadoLugar.RECHAZADO) {
                moderador.lugares.filter { it != lugar.id }
            } else {
                moderador.lugares
            }
            
            // actualizar moderador
            val moderadorActualizado = moderador.copy(
                historial = nuevoHistorial,
                lugares = nuevosLugares
            )
            
            // actualizar en la lista de moderadores
            _moderador.value = _moderador.value.map { 
                if (it.id == moderador.id) moderadorActualizado else it 
            }
            
            // actualizar moderador actual
            _moderadorActual.value = moderadorActualizado
        }
    }
}