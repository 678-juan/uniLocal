package com.example.unilocal.model.repositorio

import com.example.unilocal.model.entidad.Usuario

interface UsuarioRepositorio {
    suspend fun registrar(usuario: Usuario, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Usuario>
    suspend fun obtenerUsuario(id: String): Usuario?
    suspend fun actualizarUsuario(usuario: Usuario): Result<Unit>
    suspend fun recuperarContrasena(email: String): Result<Unit>
}