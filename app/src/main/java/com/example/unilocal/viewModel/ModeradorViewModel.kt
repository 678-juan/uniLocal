package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.Moderador
import com.example.unilocal.model.entidad.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ModeradorViewModel : ViewModel(){

    // moderadores
    private val _moderador = MutableStateFlow(emptyList<Moderador>())
    val moderador: StateFlow<List<Moderador>> = _moderador.asStateFlow()

    private val _moderadorActual = MutableStateFlow<Moderador?>(null)
    val moderadorActual: StateFlow<Moderador?> = _moderadorActual.asStateFlow()

    // historial y lugares autorizados
    private val _historial = MutableStateFlow<List<Solicitud>>(emptyList())
    val historial: StateFlow<List<Solicitud>> = _historial.asStateFlow()
    
    private val _misAutorizados = MutableStateFlow<List<Lugar>>(emptyList())
    val misAutorizados: StateFlow<List<Lugar>> = _misAutorizados.asStateFlow()

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
        _historial.value = emptyList()
        _misAutorizados.value = emptyList()
    }

    // moderar lugares
    fun registrarDecision(
        lugar: Lugar,
        moderadorId: String,
        nuevaDecision: EstadoLugar,
        motivo: String = ""
    ) {
        val solicitud = Solicitud(
            lugarId = lugar.id,
            lugarNombre = lugar.nombre,
            moderadorId = moderadorId,
            accion = nuevaDecision,
            motivo = motivo,
            fechaIso = System.currentTimeMillis().toString()
        )
        
        _historial.value = _historial.value + solicitud
        
        if (nuevaDecision == EstadoLugar.AUTORIZADO) {
            _misAutorizados.value = _misAutorizados.value + lugar
        }
    }
}