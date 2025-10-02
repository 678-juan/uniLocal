package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Moderador(
    val id: String ,
    val nombre: String ,
    val username: String ,
    val email: String,
    val clave: String

)