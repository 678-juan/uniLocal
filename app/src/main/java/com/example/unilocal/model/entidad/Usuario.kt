package com.example.unilocal.model.entidad
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String ,  //firebase medio soluciona la id
    val nombre: String ,
    val username: String ,
    val clave: String , //mirar con firebase mas adelante
    val email: String ,
    val ciudad: String ,
    val sexo: String ,
    val avatar: Int = 0, // ID del recurso de avatar seleccionado
    val favoritos: List<Lugar> = emptyList()

)
