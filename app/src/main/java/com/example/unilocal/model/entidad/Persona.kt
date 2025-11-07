package com.example.unilocal.model.entidad

import kotlinx.serialization.Serializable

@Serializable
open class Persona(
    val id: String,
    val nombre: String,
    val username: String,
    val email: String,
    val clave: String
)

