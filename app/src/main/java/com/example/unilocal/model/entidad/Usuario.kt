package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    var id: String = "",  //firebase medio soluciona la id
    val nombre: String,
    val username: String,
    val clave: String,
    val email: String,
    val ciudad: String,
    val sexo: String,
    val avatar: Int = 0,
    val favoritos: List<Lugar> = emptyList()

)

