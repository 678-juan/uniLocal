package com.example.unilocal.model.repositorioFireBase

import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.repositorio.LugarRepositorio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
class LugarRepositorioFirebase(
    private val db: FirebaseFirestore
) : LugarRepositorio {

    override suspend fun crearLugar(lugar: Lugar): Result<Unit> {
        return try {
            db.collection("lugares").document(lugar.id).set(lugar).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun eliminarLugar(id: String): Result<Unit> {
        return try {
            db.collection("lugares").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerLugares(): List<Lugar> {
        return db.collection("lugares").get().await().toObjects(Lugar::class.java)
    }

    override suspend fun obtenerLugarPorId(id: String): Lugar? {
        return db.collection("lugares").document(id).get().await().toObject(Lugar::class.java)
    }

    override suspend fun buscarLugares(filtro: String): List<Lugar> {
        return db.collection("lugares")
            .whereEqualTo("categoria", filtro)
            .get().await()
            .toObjects(Lugar::class.java)
    }

    override suspend fun actualizarEstadoLugar(id: String, estado: EstadoLugar): Result<Unit> {
        return try {
            db.collection("lugares").document(id).update("estado", estado).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
        **/