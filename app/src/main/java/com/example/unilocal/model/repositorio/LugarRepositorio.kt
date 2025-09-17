package com.example.unilocal.model.repositorio

import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar


interface LugarRepositorio {
    suspend fun crearLugar(lugar: Lugar): Result<Unit>
    suspend fun eliminarLugar(id: String): Result<Unit>
    suspend fun obtenerLugares(): List<Lugar>
    suspend fun obtenerLugarPorId(id: String): Lugar?
    suspend fun buscarLugares(filtro: String): List<Lugar>
    suspend fun actualizarEstadoLugar(id: String, estado: EstadoLugar): Result<Unit>
}