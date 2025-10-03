package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Comentario(
    val id: String ,
    val usuarioId: String ,
    val lugarId: String ,
    val texto: String ,
    val estrellas: Int ,
    val comentarios: List<String> = emptyList(),
    val fecha: Long = System.currentTimeMillis() // para la hora actual
)
