package com.example.unilocal.model.repositorio

import com.example.unilocal.model.entidad.Comentario

interface ComentarioRepositorio {
    suspend fun agregarComentario(comentario: Comentario): Result<Unit>
    suspend fun obtenerComentariosPorLugar(lugarId: String): List<Comentario>
}