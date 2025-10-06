package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ModeracionViewModel : ViewModel() {
    private val _historial = MutableStateFlow<List<Solicitud>>(emptyList())
    val historial: StateFlow<List<Solicitud>> = _historial.asStateFlow()

    private val _misAutorizados = MutableStateFlow<List<Lugar>>(emptyList())
    val misAutorizados: StateFlow<List<Lugar>> = _misAutorizados.asStateFlow()

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


