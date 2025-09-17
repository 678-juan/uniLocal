package com.example.unilocal.model.repositorioFireBase

import com.example.unilocal.model.repositorio.UsuarioRepositorio
import com.example.unilocal.model.entidad.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class UsuarioRepositorioFirebase(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : UsuarioRepositorio {

    override suspend fun registrar(usuario: Usuario, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(usuario.email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("Error al registrar"))
            val nuevoUsuario = usuario.copy(id = userId)
            db.collection("usuarios").document(userId).set(nuevoUsuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("Usuario no encontrado"))
            val snapshot = db.collection("usuarios").document(userId).get().await()
            val usuario = snapshot.toObject(Usuario::class.java) ?: return Result.failure(Exception("Datos no encontrados"))
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerUsuario(id: String): Usuario? {
        return db.collection("usuarios").document(id).get().await()
            .toObject(Usuario::class.java)
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            db.collection("usuarios").document(usuario.id).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recuperarContrasena(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}