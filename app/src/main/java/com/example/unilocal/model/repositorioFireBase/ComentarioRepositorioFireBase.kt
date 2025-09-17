package com.example.unilocal.model.repositorioFireBase



import com.example.unilocal.model.entidad.Comentario
import com.example.unilocal.model.repositorio.ComentarioRepositorio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class ComentarioRepositorioFirebase(
    private val db: FirebaseFirestore
) : ComentarioRepositorio {

    override suspend fun agregarComentario(comentario: Comentario): Result<Unit> {
        return try {
            db.collection("comentarios").document(comentario.id).set(comentario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerComentariosPorLugar(lugarId: String): List<Comentario> {
        return db.collection("comentarios")
            .whereEqualTo("lugarId", lugarId)
            .get().await()
            .toObjects(Comentario::class.java)
    }
}