package com.example.unilocal.model.entidad


data class Comentario(
    val id: String ,
    val usuarioId: String ,
    val lugarId: String ,
    val texto: String ,
    val estrellas: Int ,
    val fecha: Long = System.currentTimeMillis() //par ala hora actual
)
