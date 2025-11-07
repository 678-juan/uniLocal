package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Lugar(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val categoria: String,
    val horario: Map<String, Pair<String, String>> = emptyMap(),
    val telefono: String,
    val imagenUri: String,
    var likes: Int,

    val estado: EstadoLugar = EstadoLugar.PENDIENTE,
    val creadorId: String,
    val calificacionPromedio: Double,
    val ubicacion: Ubicacion,
    val comentarios: List<Comentario> = emptyList()
)










