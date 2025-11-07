package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unilocal.model.entidad.*
import com.example.unilocal.utils.RequestResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri
import java.util.UUID
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap

class LugaresViewModel : ViewModel() {
    private val _lugares = MutableStateFlow<List<Lugar>>(emptyList())
    val lugares: StateFlow<List<Lugar>> = _lugares.asStateFlow()

    // Estado del resultado de operaciones
    private val _lugarResult = MutableStateFlow<RequestResult?>(null)
    val lugarResult: StateFlow<RequestResult?> = _lugarResult.asStateFlow()

    // Referencia al UsuarioViewModel para sincronizar likes
    private var usuarioViewModel: UsuarioViewModel? = null

    val db = Firebase.firestore

    fun resetear() {
        _lugarResult.value = null
    }

    fun setUsuarioViewModel(usuarioViewModel: UsuarioViewModel) {
        this.usuarioViewModel = usuarioViewModel
    }

    init {
        // Cargar lugares desde Firebase
        cargarLugares()
    }

    private fun cargarLugares() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("Lugares").get().await()
                val lugaresList = snapshot.documents.mapNotNull { doc ->
                    try {
                        // Leer datos directamente del documento para manejar tipos complejos
                        val data = doc.data ?: emptyMap<String, Any>()
                        val horarioFirebase = data["horario"] as? Map<String, Any>
                        val horario = horarioDesdeFirebase(horarioFirebase)

                        // Obtener estado como String y convertirlo a enum
                        val estadoStr = data["estado"] as? String ?: "PENDIENTE"
                        val estado = try {
                            EstadoLugar.valueOf(estadoStr)
                        } catch (e: Exception) {
                            EstadoLugar.PENDIENTE
                        }

                        // Obtener ubicación
                        val ubicacionData = data["ubicacion"] as? Map<String, Any>?
                        val ubicacion = ubicacionData?.let {
                            Ubicacion(
                                latitud = (it["latitud"] as? Number)?.toDouble() ?: 0.0,
                                longitud = (it["longitud"] as? Number)?.toDouble() ?: 0.0
                            )
                        } ?: Ubicacion(0.0, 0.0)

                        // Crear objeto Lugar manualmente
                        val lugar = Lugar(
                            id = doc.id,
                            nombre = data["nombre"] as? String ?: "",
                            descripcion = data["descripcion"] as? String ?: "",
                            direccion = data["direccion"] as? String ?: "",
                            categoria = data["categoria"] as? String ?: "",
                            horario = horario,
                            telefono = data["telefono"] as? String ?: "",
                            imagenUri = data["imagenUri"] as? String ?: "",
                            likes = (data["likes"] as? Number)?.toInt() ?: 0,
                            estado = estado,
                            creadorId = data["creadorId"] as? String ?: "",
                            calificacionPromedio = (data["calificacionPromedio"] as? Number)?.toDouble() ?: 0.0,
                            ubicacion = ubicacion,
                            comentarios = emptyList() // Se carga desde subcolección
                        )

                        // Cargar comentarios desde subcolección
                        val comentarios = cargarComentariosLugar(doc.id)
                        lugar.copy(comentarios = comentarios)
                    } catch (e: Exception) {
                        // Si falla la lectura manual, intentar con toObject como fallback
                        val lugar = doc.toObject(Lugar::class.java)
                        lugar?.let {
                            val comentarios = cargarComentariosLugar(doc.id)
                            it.copy(id = doc.id, comentarios = comentarios)
                        }
                    }
                }
                _lugares.value = lugaresList
            } catch (e: Exception) {
                // Error al cargar lugares
                _lugares.value = emptyList()
            }
        }
    }

    private suspend fun cargarComentariosLugar(lugarId: String): List<Comentario> {
        return try {
            val snapshot = db.collection("Lugares")
                .document(lugarId)
                .collection("comentarios")
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: emptyMap<String, Any>()
                Comentario(
                        id = doc.id,
                        usuarioId = data["usuarioId"] as? String ?: "",
                        lugarId = data["lugarId"] as? String ?: "",
                        texto = data["texto"] as? String ?: "",
                        estrellas = (data["estrellas"] as? Number)?.toInt() ?: 0,
                        fecha = (data["fecha"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        respuesta = data["respuesta"] as? String
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Error al cargar comentarios: ${e.message}")
            emptyList()
        }
    }

    // Convertir horario de Map<String, Pair<String, String>> a formato compatible con Firestore
    private fun horarioParaFirebase(horario: Map<String, Pair<String, String>>): Map<String, Map<String, String>> {
        return horario.mapValues { (_, pair) ->
            mapOf(
                "apertura" to pair.first,
                "cierre" to pair.second
            )
        }
    }

    // Convertir horario de Firestore a Map<String, Pair<String, String>>
    private fun horarioDesdeFirebase(horarioFirebase: Map<String, Any>?): Map<String, Pair<String, String>> {
        if (horarioFirebase == null) return emptyMap()

        return horarioFirebase.mapNotNull { (dia, valor) ->
            if (valor is Map<*, *>) {
                val apertura = valor["apertura"] as? String ?: ""
                val cierre = valor["cierre"] as? String ?: ""
                dia to Pair(apertura, cierre)
            } else {
                null
            }
        }.toMap()
    }

    fun crearLugar(lugar: Lugar, context: android.content.Context? = null) {
        viewModelScope.launch {
            _lugarResult.value = RequestResult.Cargar
            try {
                // Subir imagen si es un URI local y obtener URL pública
                val imagenUrlFinal = try {
                    obtenerImagenUrlFinal(lugar.imagenUri, context)
                } catch (e: Exception) {
                    // Log del error para debugging
                    println("Error al subir imagen: ${e.message}")
                    e.printStackTrace()
                    // Si falla la subida, usar default_image
                    "default_image"
                }

                // Preparar datos para Firebase con conversiones necesarias
                println("DEBUG: Preparando datos para guardar lugar: ${lugar.nombre}")
                val lugarData = hashMapOf<String, Any>(
                    "nombre" to lugar.nombre,
                    "descripcion" to lugar.descripcion,
                    "direccion" to lugar.direccion,
                    "categoria" to lugar.categoria,
                    "horario" to horarioParaFirebase(lugar.horario),
                    "telefono" to lugar.telefono,
                    "imagenUri" to imagenUrlFinal,
                    "likes" to lugar.likes,
                    "longitud" to lugar.ubicacion.longitud,
                    "estado" to lugar.estado.name, // Guardar enum como String
                    "creadorId" to lugar.creadorId,
                    "calificacionPromedio" to lugar.calificacionPromedio,
                    "ubicacion" to hashMapOf(
                        "latitud" to lugar.ubicacion.latitud,
                        "longitud" to lugar.ubicacion.longitud
                    )
                )

                println("DEBUG: Intentando guardar en Firestore...")
                val docRef = db.collection("Lugares")
                    .add(lugarData)
                    .await()
                println("DEBUG: Documento guardado en Firestore con ID: ${docRef.id}")

                // Actualizar con el ID de Firestore
                val lugarConId = lugar.copy(id = docRef.id, imagenUri = imagenUrlFinal)

                // Guardar comentarios en subcolección si existen
                if (lugar.comentarios.isNotEmpty()) {
                    lugar.comentarios.forEach { comentario ->
                        // Evitar guardar comentarios con texto vacío
                        if (comentario.texto.isBlank()) {
                            println("DEBUG: Se omitió guardar un comentario vacío al crear lugar")
                            return@forEach
                        }

                        // Construir el mapa de datos y solo incluir 'respuesta' si no está vacía
                        val comentarioData = hashMapOf<String, Any>(
                            "usuarioId" to comentario.usuarioId,
                            "lugarId" to comentario.lugarId,
                            "texto" to comentario.texto,
                            "estrellas" to comentario.estrellas,
                            "fecha" to comentario.fecha
                        )

                        val respuestaVal = comentario.respuesta
                        if (!respuestaVal.isNullOrBlank()) {
                            comentarioData["respuesta"] = respuestaVal
                        }

                        db.collection("Lugares")
                            .document(docRef.id)
                            .collection("comentarios")
                            .add(comentarioData)
                            .await()
                    }
                }

                // Actualizar lista local
                _lugares.value = _lugares.value + lugarConId
                println("DEBUG: ✅ Lugar creado exitosamente con ID: ${docRef.id}")
                println("DEBUG: ✅ Datos guardados: nombre=${lugar.nombre}, categoria=${lugar.categoria}")

                // Marcar como éxito
                _lugarResult.value = RequestResult.Sucess("Lugar creado exitosamente")
            } catch (e: Exception) {
                // Error al crear lugar
                println("DEBUG: ❌ ERROR al crear lugar: ${e.message}")
                println("DEBUG: ❌ Tipo de error: ${e.javaClass.simpleName}")
                e.printStackTrace()
                println("DEBUG: ❌ Stack trace completo del error")

                // Marcar como error
                _lugarResult.value = RequestResult.Error("Error al crear el lugar: ${e.message ?: "Error desconocido"}")
            }
        }
    }

    // Opción 1: Subir a Firebase Storage (recomendado para imágenes grandes)
    private suspend fun obtenerImagenUrlFinal(imagenUri: String, context: android.content.Context? = null): String {
        if (imagenUri.isBlank() || imagenUri == "default_image") {
            println("DEBUG: imagenUri vacío o default_image")
            return "default_image"
        }

        // Si ya es URL, usarla tal cual
        val lower = imagenUri.lowercase()
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            println("DEBUG: imagenUri ya es URL: $imagenUri")
            return imagenUri
        }

        // Si es un URI local (content:// o file://), subir a Storage
        try {
            println("DEBUG: Intentando subir imagen a Storage: $imagenUri")
            val uri = Uri.parse(imagenUri)

            val fileName = "lugares/${UUID.randomUUID()}.jpg"
            val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

            println("DEBUG: Subiendo a Storage: $fileName")
            val uploadTask = storageRef.putFile(uri)
            uploadTask.await()

            println("DEBUG: Imagen subida, obteniendo URL de descarga...")
            val downloadUrl = storageRef.downloadUrl.await()
            val urlString = downloadUrl.toString()
            println("DEBUG: URL obtenida de Storage: $urlString")

            return urlString
        } catch (e: Exception) {
            println("DEBUG: Error al subir a Storage: ${e.message}")
            e.printStackTrace()
            // Si falla Storage, intentar Base64 como alternativa (solo si hay contexto)
            if (context != null) {
                println("DEBUG: Intentando guardar como Base64...")
                return try {
                    obtenerImagenUrlFinalBase64(imagenUri, context)
                } catch (e2: Exception) {
                    println("DEBUG: Error también con Base64: ${e2.message}")
                    "default_image"
                }
            } else {
                println("DEBUG: No hay contexto disponible para Base64, usando default_image")
                return "default_image"
            }
        }
    }

    // Opción 2: Guardar como Base64 directamente en Firestore (alternativa)
    // Útil para imágenes pequeñas o cuando Storage falla
    // Nota: Esta función requiere que se pase el contexto desde la UI
    private suspend fun obtenerImagenUrlFinalBase64(imagenUri: String, context: android.content.Context): String {
        try {
            println("DEBUG: Convirtiendo imagen a Base64: $imagenUri")
            val uri = Uri.parse(imagenUri)

            // Leer la imagen desde el URI
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("No se pudo abrir el input stream")

            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                throw Exception("No se pudo decodificar la imagen")
            }


            // Comprimir y convertir a Base64
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // 80% calidad
            val byteArray = outputStream.toByteArray()
            val base64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)

            // Prefijo especial para identificar que es Base64
            val base64Url = "data:image/jpeg;base64,$base64"
            println("DEBUG: Imagen convertida a Base64 (tamaño: ${byteArray.size} bytes)")

            return base64Url
        } catch (e: Exception) {
            println("DEBUG: Error al convertir a Base64: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    fun buscarPorId(id: String): Lugar? {
        // Primero buscar en la lista local
        val lugarLocal = _lugares.value.find { it.id == id }
        if (lugarLocal != null) return lugarLocal

        // Si no está en local, cargar desde Firebase
        viewModelScope.launch {
            try {
                val doc = db.collection("Lugares").document(id).get().await()
                if (doc.exists()) {
                    val data = doc.data!!
                    val horarioFirebase = data["horario"] as? Map<String, Any>?
                    val horario = horarioDesdeFirebase(horarioFirebase)

                    val estadoStr = data["estado"] as? String ?: "PENDIENTE"
                    val estado = try {
                        EstadoLugar.valueOf(estadoStr)
                    } catch (e: Exception) {
                        EstadoLugar.PENDIENTE
                    }

                    val ubicacionData = data["ubicacion"] as? Map<String, Any>?
                    val ubicacion = ubicacionData?.let {
                        Ubicacion(
                            latitud = (it["latitud"] as? Number)?.toDouble() ?: 0.0,
                            longitud = (it["longitud"] as? Number)?.toDouble() ?: 0.0
                        )
                    } ?: Ubicacion(0.0, 0.0)

                    val lugar = Lugar(
                        id = doc.id,
                        nombre = data["nombre"] as? String ?: "",
                        descripcion = data["descripcion"] as? String ?: "",
                        direccion = data["direccion"] as? String ?: "",
                        categoria = data["categoria"] as? String ?: "",
                        horario = horario,
                        telefono = data["telefono"] as? String ?: "",
                        imagenUri = data["imagenUri"] as? String ?: "",
                        likes = (data["likes"] as? Number)?.toInt() ?: 0,
                        estado = estado,
                        creadorId = data["creadorId"] as? String ?: "",
                        calificacionPromedio = (data["calificacionPromedio"] as? Number)?.toDouble() ?: 0.0,
                        ubicacion = ubicacion,
                        comentarios = emptyList()
                    )

                    val comentarios = cargarComentariosLugar(id)
                    val lugarCompleto = lugar.copy(comentarios = comentarios)
                    _lugares.value = _lugares.value + lugarCompleto
                }
            } catch (e: Exception) {
                // Error al buscar lugar
            }
        }
        return null
    }

    fun buscarPorCategoria(categoria: String): List<Lugar> {
        return _lugares.value.filter { it.categoria.equals(categoria, ignoreCase = true) }
    }

    fun buscarPorCreador(creadorId: String): List<Lugar> {
        return _lugares.value.filter { it.creadorId == creadorId }
    }

    fun actualizarEstado(id: String, nuevoEstado: EstadoLugar) {
        viewModelScope.launch {
            try {
        val lugar = _lugares.value.find { it.id == id }

                // Actualizar en Firebase
                db.collection("Lugares")
                    .document(id)
                    .update("estado", nuevoEstado.name)
                    .await()

                // Actualizar estado local
        _lugares.value = _lugares.value.map {
            if (it.id == id) it.copy(estado = nuevoEstado) else it
        }

        // Enviar notificación cuando se autoriza un lugar
        if (nuevoEstado == EstadoLugar.AUTORIZADO && lugar != null) {
            usuarioViewModel?.crearNotificacion(
                usuarioId = lugar.creadorId,
                titulo = "¡Lugar Autorizado!",
                mensaje = "Tu lugar '${lugar.nombre}' ha sido autorizado y ya está disponible para todos los usuarios.",
                tipo = TipoNotificacion.LUGAR_AUTORIZADO,
                lugarId = lugar.id
            )
        }

        // Enviar notificación cuando se rechaza un lugar
        if (nuevoEstado == EstadoLugar.RECHAZADO && lugar != null) {
            usuarioViewModel?.crearNotificacion(
                usuarioId = lugar.creadorId,
                titulo = "Lugar Rechazado",
                mensaje = "Tu lugar '${lugar.nombre}' ha sido rechazado. Puedes revisar los criterios y crear uno nuevo.",
                tipo = TipoNotificacion.LUGAR_RECHAZADO,
                lugarId = lugar.id
            )
                }
            } catch (e: Exception) {
                // Error al actualizar estado
            }
        }
    }

    fun borrarLugar(lugarId: String) {
        viewModelScope.launch {
            try {
                // Eliminar comentarios primero (subcolección)
                val comentariosSnapshot = db.collection("Lugares")
                    .document(lugarId)
                    .collection("comentarios")
                    .get()
                    .await()

                comentariosSnapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }

                // Eliminar lugar
                db.collection("Lugares")
                    .document(lugarId)
                    .delete()
                    .await()

                // Actualizar lista local
        _lugares.value = _lugares.value.filter { it.id != lugarId }
            } catch (e: Exception) {
                // Error al borrar lugar
            }
        }
    }

    // función para calcular si un lugar está abierto basado en el horario actual
    fun estaAbierto(lugar: Lugar): Boolean {
        val ahora = java.util.Calendar.getInstance()
        val diaActual = when (ahora.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SUNDAY -> "Domingo"
            java.util.Calendar.MONDAY -> "Lunes"
            java.util.Calendar.TUESDAY -> "Martes"
            java.util.Calendar.WEDNESDAY -> "Miércoles"
            java.util.Calendar.THURSDAY -> "Jueves"
            java.util.Calendar.FRIDAY -> "Viernes"
            java.util.Calendar.SATURDAY -> "Sábado"
            else -> "Lunes"
        }

        // buscar horario para el día actual
        val horarioHoy = lugar.horario[diaActual] ?: return false

        // si el horario es "Cerrado" o "00:00" a "00:00", está cerrado
        if (horarioHoy.first.lowercase().contains("cerrado") ||
            horarioHoy.second.lowercase().contains("cerrado") ||
            (horarioHoy.first == "00:00" && horarioHoy.second == "00:00")) {
            return false
        }

        // convertir horas a minutos para comparar correctamente
        fun horaAMinutos(hora: String): Int {
            // manejar formato AM/PM
            val horaLimpia = hora.trim()
            val esPM = horaLimpia.uppercase().contains("PM")
            val esAM = horaLimpia.uppercase().contains("AM")

            if (esPM || esAM) {
                // extraer solo la parte numérica (ej: "8:00 AM" -> "8:00")
                val patron = Regex("""(\d{1,2}):(\d{2})""")
                val match = patron.find(horaLimpia)
                if (match != null) {
                    var horas = match.groupValues[1].toInt()
                    val minutos = match.groupValues[2].toInt()

                    // convertir a formato 24h
                    if (esPM && horas != 12) {
                        horas += 12
                    } else if (esAM && horas == 12) {
                        horas = 0
                    }

                    return horas * 60 + minutos
                }
            }

            // formato 24h normal (ej: "08:00")
            val partes = horaLimpia.split(":")
            if (partes.size == 2) {
                return partes[0].toInt() * 60 + partes[1].toInt()
            }

            return 0
        }

        val horaActual = ahora.get(java.util.Calendar.HOUR_OF_DAY) * 60 + ahora.get(java.util.Calendar.MINUTE)
        val horaApertura = horaAMinutos(horarioHoy.first)
        val horaCierre = horaAMinutos(horarioHoy.second)

        // manejar horarios que cruzan medianoche (ej: 18:00 a 02:00)
        if (horaCierre < horaApertura) {
            return horaActual >= horaApertura || horaActual <= horaCierre
        }

        return horaActual >= horaApertura && horaActual <= horaCierre
    }

    fun agregarComentario(lugarId: String, comentario: Comentario) {
        viewModelScope.launch {
            try {
                // Evitar guardar comentarios con texto vacío
                if (comentario.texto.isBlank()) {
                    println("DEBUG: Se evitó guardar un comentario vacío para lugarId=$lugarId")
                    return@launch
                }

                // Guardar comentario en Firebase (subcolección) - guardar manualmente
                val comentarioData = hashMapOf<String, Any>(
                    "usuarioId" to comentario.usuarioId,
                    "lugarId" to comentario.lugarId,
                    "texto" to comentario.texto,
                    "estrellas" to comentario.estrellas,
                    "fecha" to comentario.fecha
                )

                val respuestaVal = comentario.respuesta
                if (!respuestaVal.isNullOrBlank()) {
                    comentarioData["respuesta"] = respuestaVal
                }

                val docRef = db.collection("Lugares")
                    .document(lugarId)
                    .collection("comentarios")
                    .add(comentarioData)
                    .await()

                val comentarioConId = comentario.copy(id = docRef.id)

                // Actualizar estado local
        _lugares.value = _lugares.value.map { lugar ->
            if (lugar.id == lugarId) {
                        lugar.copy(comentarios = lugar.comentarios + comentarioConId)
            } else {
                lugar
                    }
                }

                // Enviar notificación al creador del lugar
                val lugar = _lugares.value.find { it.id == lugarId }
                if (lugar != null && lugar.creadorId != comentario.usuarioId) {
                    usuarioViewModel?.crearNotificacion(
                        usuarioId = lugar.creadorId,
                        titulo = "Nuevo comentario",
                        mensaje = "Tienes un nuevo comentario en tu lugar '${lugar.nombre}'",
                        tipo = TipoNotificacion.COMENTARIO_NUEVO,
                        lugarId = lugarId
                    )
                }
            } catch (e: Exception) {
                // Error al agregar comentario
            }
        }
    }

    fun darLike(lugarId: String) {
        viewModelScope.launch {
            try {
                val lugar = _lugares.value.find { it.id == lugarId }
                if (lugar != null) {
                    // Actualizar contador en Firebase
                    db.collection("Lugares")
                        .document(lugarId)
                        .update("likes", lugar.likes + 1)
                        .await()

                    // Actualizar estado local
                    _lugares.value = _lugares.value.map { l ->
                        if (l.id == lugarId) {
                            l.copy(likes = l.likes + 1)
            } else {
                            l
                        }
            }
        }

        // Delegar la gestión de likes del usuario al UsuarioViewModel
        usuarioViewModel?.darLike(lugarId)
            } catch (e: Exception) {
                // Error al dar like
            }
        }
    }

    fun quitarLike(lugarId: String) {
        viewModelScope.launch {
            try {
                val lugar = _lugares.value.find { it.id == lugarId }
                if (lugar != null) {
                    // Actualizar contador en Firebase
                    val nuevosLikes = maxOf(0, lugar.likes - 1)
                    db.collection("Lugares")
                        .document(lugarId)
                        .update("likes", nuevosLikes)
                        .await()

                    // Actualizar estado local
                    _lugares.value = _lugares.value.map { l ->
                        if (l.id == lugarId) {
                            l.copy(likes = nuevosLikes)
            } else {
                            l
                        }
            }
        }

        // Delegar la gestión de likes del usuario al UsuarioViewModel
        usuarioViewModel?.quitarLike(lugarId)
            } catch (e: Exception) {
                // Error al quitar like
            }
        }
    }

    fun yaDioLike(lugarId: String): Boolean {
        // Delegar la verificación al UsuarioViewModel
        return usuarioViewModel?.yaDioLike(lugarId) ?: false
    }


    fun responderComentario(lugarId: String, comentarioId: String, respuesta: String) {
        viewModelScope.launch {
            try {
                // Evitar guardar respuestas vacías
                if (respuesta.isBlank()) {
                    println("DEBUG: Se evitó guardar una respuesta vacía para comentarioId=$comentarioId")
                    return@launch
                }

                // Actualizar respuesta en Firebase
                db.collection("Lugares")
                    .document(lugarId)
                    .collection("comentarios")
                    .document(comentarioId)
                    .update("respuesta", respuesta)
                    .await()

                // Actualizar estado local
        _lugares.value = _lugares.value.map { lugar ->
            if (lugar.id == lugarId) {
                lugar.copy(
                    comentarios = lugar.comentarios.map { comentario ->
                        if (comentario.id == comentarioId) {
                            comentario.copy(respuesta = respuesta)
                        } else {
                            comentario
                        }
                    }
                )
            } else {
                lugar
                    }
                }
            } catch (e: Exception) {
                // Error al responder comentario
            }
        }
    }

}

