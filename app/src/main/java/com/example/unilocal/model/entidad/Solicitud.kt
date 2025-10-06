package com.example.unilocal.model.entidad

import kotlinx.serialization.Serializable

@Serializable
data class Solicitud(
    val lugarId: String,
    val lugarNombre: String,
    val moderadorId: String,
    val accion: EstadoLugar, // AUTORIZADO o RECHAZADO
    val motivo: String,
    val fechaIso: String
)
