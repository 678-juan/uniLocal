package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class ModeracionRegistro(
    val lugarId: String,
    val lugarNombre: String,
    val moderadorId: String,
    val accion: EstadoLugar, // AUTORIZADO o RECHAZADO
    val motivo: String,
    val fechaIso: String
)

class ModeracionViewModel : ViewModel() {
    private val _historial = MutableStateFlow<List<ModeracionRegistro>>(emptyList())
    val historial: StateFlow<List<ModeracionRegistro>> = _historial.asStateFlow()

    private val _misAutorizados = MutableStateFlow<List<Lugar>>(emptyList())
    val misAutorizados: StateFlow<List<Lugar>> = _misAutorizados.asStateFlow()

    fun registrarDecision(
        lugar: Lugar,
        moderadorId: String,
        nuevaDecision: EstadoLugar,
        motivo: String = ""
    ) {
        val registro = ModeracionRegistro(
            lugarId = lugar.id,
            lugarNombre = lugar.nombre,
            moderadorId = moderadorId,
            accion = nuevaDecision,
            motivo = motivo,
            fechaIso = System.currentTimeMillis().toString()
        )
        _historial.value = _historial.value + registro
        if (nuevaDecision == EstadoLugar.AUTORIZADO) {
            _misAutorizados.value = _misAutorizados.value + lugar
        }
    }
}


