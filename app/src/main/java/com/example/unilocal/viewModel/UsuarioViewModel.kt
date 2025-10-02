package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel : ViewModel() {
    private val _usuario = MutableStateFlow(emptyList<Usuario>())
    val usuario: StateFlow<List<Usuario>> = _usuario.asStateFlow()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {

        val usuario1 = Usuario("1", "Juan", "juan123", "clave123", "william.henry.moody@my-own-personal-domain.com", "Ciudad A", "Masculino")
        val usuario2 = Usuario("2", "María", "maria456", "clave456", "james.francis.byrnes@example-pet-store.com", "Ciudad B", "Femenino")
        val usuario3 = Usuario("3", "Pedro", "pedro789", "clave789", "william.henry.moody@my-own-personal-domain.com", "Ciudad C", "Masculino")

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
        return _usuario.value.find { it.email == email && it.clave == password }
    }






}




