package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.Moderador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ModeradorViewModel : ViewModel(){

    private val _moderador = MutableStateFlow(emptyList<Moderador>())
    val moderador: StateFlow<List<Moderador>> = _moderador.asStateFlow()

    init{
        cargarMoeradores()
    }

    fun cargarMoeradores(){
        val moderador1 = Moderador("1", "Juan", "juan123", "juan.moderador@gmail.com", "clave123")
        val moderador2 = Moderador("2", "María", "maria456", "maria.moderador@gmail.com", "clave456")
        val moderador3 = Moderador("3", "Pedro", "pedro789", "pedro.moderador@gmail.com", "clave789")

        _moderador.value = listOf(moderador1, moderador2, moderador3)
    }
    fun crearModerador(moderador: Moderador){
        _moderador.value += moderador
    }
    fun buscarId(id: String): Moderador? {
        return _moderador.value.find { it.id == id }
    }

    fun buscarEmail(email: String): Moderador? {
        return _moderador.value.find { it.email == email }
    }

    fun login(email: String, password: String): Moderador? {
        return _moderador.value.find { it.email == email && it.clave == password }

    }
}