package com.example.unilocal.model.entidad

import kotlinx.serialization.Serializable

@Serializable
data class Notificacion(
    val id: String,
    val usuarioId: String,
    val titulo: String,
    val mensaje: String,
    val tipo: TipoNotificacion,
    val lugarId: String? = null,
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false
)


