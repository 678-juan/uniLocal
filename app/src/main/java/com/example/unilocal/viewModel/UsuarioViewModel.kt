package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel : ViewModel() {
    private val _usuario = MutableStateFlow(emptyList<Usuario>())
    val usuario: StateFlow<List<Usuario>> = _usuario.asStateFlow()
    
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

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
        }
        return usuarioEncontrado
    }
    
    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun agregarFavorito(lugar: com.example.unilocal.model.entidad.Lugar) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            val favoritosActualizados = usuario.favoritos + lugar
            val usuarioActualizado = usuario.copy(favoritos = favoritosActualizados)
            _usuarioActual.value = usuarioActualizado
            
            // Actualizar también en la lista de usuarios
            _usuario.value = _usuario.value.map { 
                if (it.id == usuario.id) usuarioActualizado else it 
            }
        }
    }

    fun quitarFavorito(lugarId: String) {
        val usuario = _usuarioActual.value
        if (usuario != null) {
            val favoritosActualizados = usuario.favoritos.filter { it.id != lugarId }
            val usuarioActualizado = usuario.copy(favoritos = favoritosActualizados)
            _usuarioActual.value = usuarioActualizado
            
            // Actualizar también en la lista de usuarios
            _usuario.value = _usuario.value.map { 
                if (it.id == usuario.id) usuarioActualizado else it 
            }
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
            
            // Actualizar también en la lista de usuarios
            _usuario.value = _usuario.value.map { 
                if (it.id == usuario.id) usuarioActualizado else it 
            }
        }
    }
}