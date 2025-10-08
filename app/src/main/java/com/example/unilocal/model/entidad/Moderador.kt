package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Moderador(
    val id: String ,
    val nombre: String ,
    val username: String ,
    val email: String,
    val clave: String,
    val historial: List<Solicitud> = emptyList(),
    val lugares: List<String> = emptyList() // ids lugares agarro
)