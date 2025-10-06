package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Comentario(
    val id: String,
    val usuarioId: String,
    val lugarId: String,
    val texto: String,
    val estrellas: Int,
    val fecha: Long = System.currentTimeMillis()
)
