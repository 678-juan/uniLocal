package com.example.unilocal.model.entidad

data class Usuario(
    val id: String ,
    val nombre: String ,
    val username: String ,
    val email: String ,
    val ciudad: String ,
    val favoritos: List<String> = emptyList()
)
