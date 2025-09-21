package com.example.unilocal.model.entidad

data class Usuario(
    val id: String ,  //firebase medio soluciona la id
    val nombre: String ,
    val username: String ,
    //val clave: String , mirar la seguridad e firebase
    val email: String ,
    val ciudad: String ,
    val sexo: String ,
    val favoritos: List<String> = emptyList()
)
