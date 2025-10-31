package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unilocal.model.entidad.Usuario
import com.example.unilocal.model.entidad.Notificacion
import com.example.unilocal.model.entidad.TipoNotificacion
import com.example.unilocal.utils.RequestResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsuarioViewModel : ViewModel() {
    private val _usuario = MutableStateFlow(emptyList<Usuario>())
    val usuario: StateFlow<List<Usuario>> = _usuario.asStateFlow()
    
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()
    
    // likes por usuario - Map<usuarioId, Set<lugarId>>
    private val _likesPorUsuario = MutableStateFlow(mapOf<String, Set<String>>())
    val likesPorUsuario: StateFlow<Map<String, Set<String>>> = _likesPorUsuario.asStateFlow()
    
    // likes del usuario actual - se actualiza dinámicamente
    private val _likesDados = MutableStateFlow(setOf<String>())
    val likesDados: StateFlow<Set<String>> = _likesDados.asStateFlow()
    
    // favoritos
    private val _favoritosGuardados = MutableStateFlow(setOf<String>())
    val favoritosGuardados: StateFlow<Set<String>> = _favoritosGuardados.asStateFlow()
    
    // notificaciones por usuario - Map<usuarioId, List<Notificacion>>
    private val _notificacionesPorUsuario = MutableStateFlow(mapOf<String, List<Notificacion>>())
    val notificacionesPorUsuario: StateFlow<Map<String, List<Notificacion>>> = _notificacionesPorUsuario.asStateFlow()
    
    // notificaciones del usuario actual
    private val _notificacionesUsuario = MutableStateFlow(emptyList<Notificacion>())
    val notificacionesUsuario: StateFlow<List<Notificacion>> = _notificacionesUsuario.asStateFlow()

    private val _usuarioResult = MutableStateFlow<RequestResult?>(null)
    val usuarioResult: StateFlow<RequestResult?> = _usuarioResult.asStateFlow()

    val db = Firebase.firestore
    
    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("Usuarios").get().await()
                val usuarios = snapshot.documents.mapNotNull { doc ->
                    if (doc.exists()) {
                        val data = doc.data!!
                        Usuario(
                            id = doc.id,
                            nombre = data["nombre"] as? String ?: "",
                            username = data["username"] as? String ?: "",
                            clave = data["clave"] as? String ?: "",
                            email = data["email"] as? String ?: "",
                            ciudad = data["ciudad"] as? String ?: "",
                            sexo = data["sexo"] as? String ?: "",
                            avatar = when (val avatarValue = data["avatar"]) {
                    is Int -> avatarValue
                    is Long -> avatarValue.toInt()
                    is Number -> avatarValue.toInt()
                    else -> 0
                },
                            favoritos = emptyList() // Los favoritos se manejan en subcolección
                        )
                    } else {
                        null
                    }
                }
                _usuario.value = usuarios
            } catch (e: Exception) {
                // Error al cargar usuarios, lista vacía
            }
        }
    }

    fun crearUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _usuarioResult.value = RequestResult.Cargar
            _usuarioResult.value = runCatching { crearUsuarioFireBase(usuario) }.fold(
                onSuccess = { RequestResult.Sucess("Usuario creado exitosamente") },
                onFailure = { RequestResult.Error(it.message ?: "Error creando usuario") }
            )
        }


    }

    suspend fun crearUsuarioFireBase(usuario: Usuario) {
        // Guardar usuario sin favoritos (se manejan en subcolección)
        val usuarioData = hashMapOf<String, Any>(
            "nombre" to usuario.nombre,
            "username" to usuario.username,
            "clave" to usuario.clave,
            "email" to usuario.email,
            "ciudad" to usuario.ciudad,
            "sexo" to usuario.sexo,
            "avatar" to usuario.avatar.toLong() // Guardar como Long para compatibilidad con Firestore
            // favoritos NO se guarda aquí, se maneja en subcolección
        )
        
        val docRef = db.collection("Usuarios")
            .add(usuarioData)
            .await()
        
        // Actualizar lista local con el usuario y su ID de Firestore
        val usuarioConId = usuario.copy(id = docRef.id, favoritos = emptyList())
        _usuario.value = _usuario.value + usuarioConId
    }

    // Buscar usuario síncronamente en la lista local (para uso en UI)
    fun obtenerUsuarioPorId(id: String): Usuario? {
        return _usuario.value.find { it.id == id }
    }
    
    fun buscarId(id: String){
        viewModelScope.launch {
            _usuarioResult.value = RequestResult.Cargar
            _usuarioResult.value = runCatching { buscarIdFireBase(id) }.fold(
                onSuccess = { RequestResult.Sucess("Usuario encontrado") },
                onFailure = { RequestResult.Error(it.message ?: "Error buscando usuario") }
            )
        }
    }

    suspend fun buscarIdFireBase(id: String) {
        val snapshot = db.collection("Usuarios").document(id).get().await()

        if (snapshot.exists()) {
            val data = snapshot.data!!
            val usuario = Usuario(
                id = snapshot.id,
                nombre = data["nombre"] as? String ?: "",
                username = data["username"] as? String ?: "",
                clave = data["clave"] as? String ?: "",
                email = data["email"] as? String ?: "",
                ciudad = data["ciudad"] as? String ?: "",
                sexo = data["sexo"] as? String ?: "",
                avatar = when (val avatarValue = data["avatar"]) {
                    is Int -> avatarValue
                    is Long -> avatarValue.toInt()
                    is Number -> avatarValue.toInt()
                    else -> 0
                },
                favoritos = emptyList() // Se carga desde subcolección
            )
            
            _usuarioActual.value = usuario
            
            // Agregar a la lista local si no existe
            if (!_usuario.value.any { it.id == usuario.id }) {
                _usuario.value = _usuario.value + usuario
            }
            
            // Cargar datos relacionados del usuario
            cargarLikesUsuarioFirebase(id)
            cargarFavoritosUsuarioFirebase(id)
            cargarNotificacionesUsuarioFirebase(id)
        }
    }

    fun buscarEmail(email: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("Usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .await()
                
                val doc = snapshot.documents.firstOrNull()
                if (doc != null && doc.exists()) {
                    val data = doc.data!!
                    val usuario = Usuario(
                        id = doc.id,
                        nombre = data["nombre"] as? String ?: "",
                        username = data["username"] as? String ?: "",
                        clave = data["clave"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        ciudad = data["ciudad"] as? String ?: "",
                        sexo = data["sexo"] as? String ?: "",
                        avatar = when (val avatarValue = data["avatar"]) {
                    is Int -> avatarValue
                    is Long -> avatarValue.toInt()
                    is Number -> avatarValue.toInt()
                    else -> 0
                },
                        favoritos = emptyList() // Se carga desde subcolección
                    )
                    
                    _usuarioActual.value = usuario
                    
                    // Agregar a la lista local si no existe
                    if (!_usuario.value.any { it.id == usuario.id }) {
                        _usuario.value = _usuario.value + usuario
                    }
                    
                    cargarLikesUsuarioFirebase(usuario.id)
                    cargarFavoritosUsuarioFirebase(usuario.id)
                    cargarNotificacionesUsuarioFirebase(usuario.id)
                }
            } catch (e: Exception) {
                // Error al buscar
            }
        }
    }

    suspend fun existeEmail(email: String): Boolean {
        return try {
            val snapshot = db.collection("Usuarios")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _usuarioResult.value = RequestResult.Cargar
            _usuarioResult.value = runCatching { loginFireBase(email,password) }.fold(
                onSuccess = { RequestResult.Sucess("Iniciado sesion exitosamente") },
                onFailure = { RequestResult.Error(it.message ?: "Error al iniciar sesion") }
            )
        }
    }

    suspend fun loginFireBase(email: String, password: String){
        val snapshot = db.collection("Usuarios")
            .whereEqualTo("email", email)
            .whereEqualTo("clave", password)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull()
        if (doc != null && doc.exists()) {
            val data = doc.data!!
            val usuario = Usuario(
                id = doc.id,
                nombre = data["nombre"] as? String ?: "",
                username = data["username"] as? String ?: "",
                clave = data["clave"] as? String ?: "",
                email = data["email"] as? String ?: "",
                ciudad = data["ciudad"] as? String ?: "",
                sexo = data["sexo"] as? String ?: "",
                avatar = when (val avatarValue = data["avatar"]) {
                    is Int -> avatarValue
                    is Long -> avatarValue.toInt()
                    is Number -> avatarValue.toInt()
                    else -> 0
                },
                favoritos = emptyList() // Se carga desde subcolección
            )
            
            _usuarioActual.value = usuario
            
            // Agregar a la lista local si no existe
            if (!_usuario.value.any { it.id == usuario.id }) {
                _usuario.value = _usuario.value + usuario
            }
            
            // Cargar datos relacionados del usuario
            cargarLikesUsuarioFirebase(usuario.id)
            cargarFavoritosUsuarioFirebase(usuario.id)
            cargarNotificacionesUsuarioFirebase(usuario.id)
        } else {
            throw Exception("Usuario no encontrado")
        }
    }
    
    fun cerrarSesion() {
        _usuarioActual.value = null
        _likesDados.value = emptySet()
        _notificacionesUsuario.value = emptyList()
    }
    
    private suspend fun cargarLikesUsuarioFirebase(usuarioId: String) {
        try {
            val snapshot = db.collection("Usuarios")
                .document(usuarioId)
                .collection("likes")
                .get()
                .await()
            
            val likesIds = snapshot.documents.map { it.id }.toSet()
            _likesDados.value = likesIds
            
            // Actualizar mapa de likes por usuario
            val likesPorUsuarioActual = _likesPorUsuario.value.toMutableMap()
            likesPorUsuarioActual[usuarioId] = likesIds
            _likesPorUsuario.value = likesPorUsuarioActual
        } catch (e: Exception) {
            // Error al cargar likes
        }
    }
    
    private suspend fun cargarFavoritosUsuarioFirebase(usuarioId: String) {
        try {
            val snapshot = db.collection("Usuarios")
                .document(usuarioId)
                .collection("favoritos")
                .get()
                .await()
            
            val favoritosIds = snapshot.documents.map { it.id }.toSet()
            _favoritosGuardados.value = favoritosIds
        } catch (e: Exception) {
            // Error al cargar favoritos
        }
    }
    
    private suspend fun cargarNotificacionesUsuarioFirebase(usuarioId: String) {
        try {
            val snapshot = db.collection("Usuarios")
                .document(usuarioId)
                .collection("notificaciones")
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notificaciones = snapshot.documents.mapNotNull { doc ->
                if (doc.exists()) {
                    val data = doc.data!!
                    val tipoStr = data["tipo"] as? String ?: "LUGAR_AUTORIZADO"
                    val tipo = try {
                        TipoNotificacion.valueOf(tipoStr)
                    } catch (e: Exception) {
                        TipoNotificacion.LUGAR_AUTORIZADO
                    }
                    
                    Notificacion(
                        id = doc.id,
                        usuarioId = data["usuarioId"] as? String ?: "",
                        titulo = data["titulo"] as? String ?: "",
                        mensaje = data["mensaje"] as? String ?: "",
                        tipo = tipo,
                        lugarId = data["lugarId"] as? String,
                        fecha = (data["fecha"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        leida = data["leida"] as? Boolean ?: false
                    )
                } else {
                    null
                }
            }
            
            _notificacionesUsuario.value = notificaciones
            
            // Actualizar mapa de notificaciones por usuario
            val notificacionesPorUsuarioActual = _notificacionesPorUsuario.value.toMutableMap()
            notificacionesPorUsuarioActual[usuarioId] = notificaciones
            _notificacionesPorUsuario.value = notificacionesPorUsuarioActual
        } catch (e: Exception) {
            // Error al cargar notificaciones
        }
    }

    fun agregarFavorito(lugar: com.example.unilocal.model.entidad.Lugar) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            viewModelScope.launch {
                try {
                    // Guardar en Firebase como subcolección
                    db.collection("Usuarios")
                        .document(usuario.id)
                        .collection("favoritos")
                        .document(lugar.id)
                        .set(mapOf("lugarId" to lugar.id, "agregadoEn" to System.currentTimeMillis()))
                        .await()
                    
                    // Actualizar estado local
                    val favoritosActuales = _favoritosGuardados.value.toMutableSet()
                    favoritosActuales.add(lugar.id)
                    _favoritosGuardados.value = favoritosActuales
                } catch (e: Exception) {
                    // Error al agregar favorito
                }
            }
        }
    }

    fun quitarFavorito(lugar: com.example.unilocal.model.entidad.Lugar) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            viewModelScope.launch {
                try {
                    // Eliminar de Firebase
                    db.collection("Usuarios")
                        .document(usuario.id)
                        .collection("favoritos")
                        .document(lugar.id)
                        .delete()
                        .await()
                    
                    // Actualizar estado local
                    val favoritosActuales = _favoritosGuardados.value.toMutableSet()
                    favoritosActuales.remove(lugar.id)
                    _favoritosGuardados.value = favoritosActuales
                } catch (e: Exception) {
                    // Error al quitar favorito
                }
            }
        }
    }

    fun actualizarUsuario(nombre: String, username: String, email: String, ciudad: String, nuevaContrasena: String? = null) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            viewModelScope.launch {
                _usuarioResult.value = RequestResult.Cargar
                _usuarioResult.value = runCatching { 
                    actualizarUsuarioFirebase(nombre, username, email, ciudad, nuevaContrasena)
                }.fold(
                    onSuccess = { RequestResult.Sucess("Usuario actualizado exitosamente") },
                    onFailure = { RequestResult.Error(it.message ?: "Error actualizando usuario") }
                )
            }
        }
    }
    
    private suspend fun actualizarUsuarioFirebase(nombre: String, username: String, email: String, ciudad: String, nuevaContrasena: String?) {
        val usuario = _usuarioActual.value ?: throw Exception("No hay usuario actual")
        val clave = nuevaContrasena ?: usuario.clave
        
        val usuarioActualizado = usuario.copy(
            nombre = nombre,
            username = username,
            email = email,
            ciudad = ciudad,
            clave = clave
        )
        
        // Actualizar en Firebase (sin favoritos, se manejan en subcolección)
        val usuarioData = hashMapOf<String, Any>(
            "nombre" to usuarioActualizado.nombre,
            "username" to usuarioActualizado.username,
            "clave" to usuarioActualizado.clave,
            "email" to usuarioActualizado.email,
            "ciudad" to usuarioActualizado.ciudad,
            "sexo" to usuarioActualizado.sexo,
            "avatar" to usuarioActualizado.avatar.toLong() // Guardar como Long para compatibilidad
        )
        
        db.collection("Usuarios")
            .document(usuario.id)
            .set(usuarioData)
            .await()
        
        // Actualizar estado local
        _usuarioActual.value = usuarioActualizado
        _usuario.value = _usuario.value.map { 
            if (it.id == usuario.id) usuarioActualizado else it 
        }
    }
    
    // likes
    fun darLike(lugarId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            viewModelScope.launch {
                try {
                    // Guardar like en Firebase como subcolección
                    db.collection("Usuarios")
                        .document(usuario.id)
                        .collection("likes")
                        .document(lugarId)
                        .set(mapOf("lugarId" to lugarId, "fecha" to System.currentTimeMillis()))
                        .await()
                    
                    // Actualizar estado local
                    val likesActuales = _likesDados.value.toMutableSet()
                    likesActuales.add(lugarId)
                    _likesDados.value = likesActuales
                    
                    // Actualizar likes por usuario
                    val likesPorUsuarioActual = _likesPorUsuario.value.toMutableMap()
                    likesPorUsuarioActual[usuario.id] = likesActuales
                    _likesPorUsuario.value = likesPorUsuarioActual
                } catch (e: Exception) {
                    // Error al dar like
                }
            }
        }
    }
    
    fun quitarLike(lugarId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            viewModelScope.launch {
                try {
                    // Eliminar like de Firebase
                    db.collection("Usuarios")
                        .document(usuario.id)
                        .collection("likes")
                        .document(lugarId)
                        .delete()
                        .await()
                    
                    // Actualizar estado local
                    val likesActuales = _likesDados.value.toMutableSet()
                    likesActuales.remove(lugarId)
                    _likesDados.value = likesActuales
                    
                    // Actualizar likes por usuario
                    val likesPorUsuarioActual = _likesPorUsuario.value.toMutableMap()
                    likesPorUsuarioActual[usuario.id] = likesActuales
                    _likesPorUsuario.value = likesPorUsuarioActual
                } catch (e: Exception) {
                    // Error al quitar like
                }
            }
        }
    }
    
    fun yaDioLike(lugarId: String): Boolean {
        return _likesDados.value.contains(lugarId)
    }
    
    fun estaGuardado(lugarId: String): Boolean {
        return _favoritosGuardados.value.contains(lugarId)
    }
    
    // Funciones para manejar notificaciones
    fun crearNotificacion(usuarioId: String, titulo: String, mensaje: String, tipo: TipoNotificacion, lugarId: String? = null) {
        viewModelScope.launch {
            try {
                val notificacion = Notificacion(
                    id = "", // Se asignará el ID de Firestore
                    usuarioId = usuarioId,
                    titulo = titulo,
                    mensaje = mensaje,
                    tipo = tipo,
                    lugarId = lugarId,
                    fecha = System.currentTimeMillis(),
                    leida = false
                )
                
                // Guardar en Firebase (convertir enum a String)
                val notificacionData = hashMapOf<String, Any>(
                    "usuarioId" to notificacion.usuarioId,
                    "titulo" to notificacion.titulo,
                    "mensaje" to notificacion.mensaje,
                    "tipo" to notificacion.tipo.name, // Guardar enum como String
                    "lugarId" to (notificacion.lugarId ?: ""),
                    "fecha" to notificacion.fecha,
                    "leida" to notificacion.leida
                )
                
                val docRef = db.collection("Usuarios")
                    .document(usuarioId)
                    .collection("notificaciones")
                    .add(notificacionData)
                    .await()
                
                val notificacionConId = notificacion.copy(id = docRef.id)
                
                // Actualizar estado local
                val notificacionesActuales = _notificacionesPorUsuario.value.toMutableMap()
                val notificacionesDelUsuario = (notificacionesActuales[usuarioId] ?: emptyList()).toMutableList()
                notificacionesDelUsuario.add(0, notificacionConId) // Agregar al inicio
                notificacionesActuales[usuarioId] = notificacionesDelUsuario
                _notificacionesPorUsuario.value = notificacionesActuales
                
                // Si es el usuario actual, actualizar también su lista
                if (_usuarioActual.value?.id == usuarioId) {
                    _notificacionesUsuario.value = notificacionesDelUsuario
                }
            } catch (e: Exception) {
                // Error al crear notificación
            }
        }
    }
    
    fun marcarNotificacionComoLeida(notificacionId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            viewModelScope.launch {
                try {
                    // Actualizar en Firebase
                    db.collection("Usuarios")
                        .document(usuario.id)
                        .collection("notificaciones")
                        .document(notificacionId)
                        .update("leida", true)
                        .await()
                    
                    // Actualizar estado local
                    val notificacionesActuales = _notificacionesPorUsuario.value.toMutableMap()
                    val notificacionesDelUsuario = (notificacionesActuales[usuario.id] ?: emptyList()).toMutableList()
                    val notificacionIndex = notificacionesDelUsuario.indexOfFirst { it.id == notificacionId }
                    if (notificacionIndex != -1) {
                        notificacionesDelUsuario[notificacionIndex] = notificacionesDelUsuario[notificacionIndex].copy(leida = true)
                        notificacionesActuales[usuario.id] = notificacionesDelUsuario
                        _notificacionesPorUsuario.value = notificacionesActuales
                        _notificacionesUsuario.value = notificacionesDelUsuario
                    }
                } catch (e: Exception) {
                    // Error al marcar notificación
                }
            }
        }
    }
    
    fun obtenerNotificacionesNoLeidas(): Int {
        return _notificacionesUsuario.value.count { !it.leida }
    }

    fun resetear(){
        _usuarioResult.value = null
    }
}