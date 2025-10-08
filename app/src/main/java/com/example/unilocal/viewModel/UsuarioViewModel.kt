package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.Usuario
import com.example.unilocal.model.entidad.Notificacion
import com.example.unilocal.model.entidad.TipoNotificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        val usuario1 = Usuario("1", "Juan Pérez", "juan123", "clave123", "juan@gmail.com", "Bogotá", "Masculino", 0)
        val usuario2 = Usuario("2", "María García", "maria456", "clave456", "maria@gmail.com", "Medellín", "Femenino", 1)
        val usuario3 = Usuario("3", "Pedro López", "pedro789", "clave789", "pedro@gmail.com", "Cali", "Masculino", 2)
        val usuario4 = Usuario("4", "Ana Martínez", "ana123", "clave123", "ana@gmail.com", "Barranquilla", "Femenino", 3)
        val usuario5 = Usuario("5", "Carlos Rodríguez", "carlos456", "clave456", "carlos@gmail.com", "Cartagena", "Masculino", 4)

        _usuario.value = listOf(usuario1, usuario2, usuario3, usuario4, usuario5)
    }

    fun crearUsuario(usuario: Usuario) {
        _usuario.value += usuario
    }

    fun buscarId(id: String): Usuario? {
        return _usuario.value.find { it.id == id }
    }

    fun buscarEmail(email: String): Usuario? {
        return _usuario.value.find { it.email == email }
    }

    fun login(email: String, password: String): Usuario? {
        val usuarioEncontrado = _usuario.value.find { it.email == email && it.clave == password }
        if (usuarioEncontrado != null) {
            _usuarioActual.value = usuarioEncontrado
            // Cargar likes del usuario actual
            cargarLikesUsuario(usuarioEncontrado.id)
            // Cargar notificaciones del usuario actual
            cargarNotificacionesUsuario(usuarioEncontrado.id)
        }
        return usuarioEncontrado
    }
    
    fun cerrarSesion() {
        _usuarioActual.value = null
        _likesDados.value = emptySet()
        _notificacionesUsuario.value = emptyList()
    }
    
    private fun cargarLikesUsuario(usuarioId: String) {
        val likesDelUsuario = _likesPorUsuario.value[usuarioId] ?: emptySet()
        _likesDados.value = likesDelUsuario
    }
    
    private fun cargarNotificacionesUsuario(usuarioId: String) {
        val notificacionesDelUsuario = _notificacionesPorUsuario.value[usuarioId] ?: emptyList()
        _notificacionesUsuario.value = notificacionesDelUsuario
    }

    fun agregarFavorito(lugar: com.example.unilocal.model.entidad.Lugar) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            val favoritosActualizados = usuario.favoritos + lugar
            val usuarioActualizado = usuario.copy(favoritos = favoritosActualizados)
            _usuarioActual.value = usuarioActualizado
            
            // actualizar lista de usuarios
            _usuario.value = _usuario.value.map { 
                if (it.id == usuario.id) usuarioActualizado else it 
            }
            
            // guardar en favoritos
            val favoritosActuales = _favoritosGuardados.value.toMutableSet()
            favoritosActuales.add(lugar.id)
            _favoritosGuardados.value = favoritosActuales
        }
    }

    fun quitarFavorito(lugar: com.example.unilocal.model.entidad.Lugar) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            val favoritosActualizados = usuario.favoritos.filter { it.id != lugar.id }
            val usuarioActualizado = usuario.copy(favoritos = favoritosActualizados)
            _usuarioActual.value = usuarioActualizado
            
            // actualizar lista de usuarios
            _usuario.value = _usuario.value.map { 
                if (it.id == usuario.id) usuarioActualizado else it 
            }
            
            // quitar de favoritos
            val favoritosActuales = _favoritosGuardados.value.toMutableSet()
            favoritosActuales.remove(lugar.id)
            _favoritosGuardados.value = favoritosActuales
        }
    }

    fun actualizarUsuario(nombre: String, username: String, email: String, ciudad: String, nuevaContrasena: String? = null) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            val clave = nuevaContrasena ?: usuario.clave
            val usuarioActualizado = usuario.copy(
                nombre = nombre,
                username = username,
                email = email,
                ciudad = ciudad,
                clave = clave
            )
            _usuarioActual.value = usuarioActualizado
            
            // actualizar lista de usuarios
            _usuario.value = _usuario.value.map { 
                if (it.id == usuario.id) usuarioActualizado else it 
            }
        }
    }
    
    // likes
    fun darLike(lugarId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            // Actualizar likes del usuario actual
            val likesActuales = _likesDados.value.toMutableSet()
            likesActuales.add(lugarId)
            _likesDados.value = likesActuales
            
            // Actualizar likes por usuario
            val likesPorUsuarioActual = _likesPorUsuario.value.toMutableMap()
            likesPorUsuarioActual[usuario.id] = likesActuales
            _likesPorUsuario.value = likesPorUsuarioActual
        }
    }
    
    fun quitarLike(lugarId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            // Actualizar likes del usuario actual
            val likesActuales = _likesDados.value.toMutableSet()
            likesActuales.remove(lugarId)
            _likesDados.value = likesActuales
            
            // Actualizar likes por usuario
            val likesPorUsuarioActual = _likesPorUsuario.value.toMutableMap()
            likesPorUsuarioActual[usuario.id] = likesActuales
            _likesPorUsuario.value = likesPorUsuarioActual
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
        val notificacion = Notificacion(
            id = "notif_${System.currentTimeMillis()}",
            usuarioId = usuarioId,
            titulo = titulo,
            mensaje = mensaje,
            tipo = tipo,
            lugarId = lugarId
        )
        
        val notificacionesActuales = _notificacionesPorUsuario.value.toMutableMap()
        val notificacionesDelUsuario = (notificacionesActuales[usuarioId] ?: emptyList()).toMutableList()
        notificacionesDelUsuario.add(0, notificacion) // Agregar al inicio
        notificacionesActuales[usuarioId] = notificacionesDelUsuario
        _notificacionesPorUsuario.value = notificacionesActuales
        
        // Si es el usuario actual, actualizar también su lista
        if (_usuarioActual.value?.id == usuarioId) {
            _notificacionesUsuario.value = notificacionesDelUsuario
        }
    }
    
    fun marcarNotificacionComoLeida(notificacionId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            val notificacionesActuales = _notificacionesPorUsuario.value.toMutableMap()
            val notificacionesDelUsuario = (notificacionesActuales[usuario.id] ?: emptyList()).toMutableList()
            val notificacionIndex = notificacionesDelUsuario.indexOfFirst { it.id == notificacionId }
            if (notificacionIndex != -1) {
                notificacionesDelUsuario[notificacionIndex] = notificacionesDelUsuario[notificacionIndex].copy(leida = true)
                notificacionesActuales[usuario.id] = notificacionesDelUsuario
                _notificacionesPorUsuario.value = notificacionesActuales
                _notificacionesUsuario.value = notificacionesDelUsuario
            }
        }
    }
    
    fun obtenerNotificacionesNoLeidas(): Int {
        return _notificacionesUsuario.value.count { !it.leida }
    }
}