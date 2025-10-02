package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Ubicacion
import com.example.unilocal.model.entidad.Comentario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.unilocal.R

class LugaresViewModel : ViewModel() {

    private val _lugares = MutableStateFlow(emptyList<Lugar>())
    val lugares: StateFlow<List<Lugar>> = _lugares.asStateFlow()

    init {
        cargarLugares()
    }

    private fun cargarLugares() {
        val lugar1 = Lugar(
            id = "l1",
            nombre = "Restaurante El Sabor",
            descripcion = "Comida típica colombiana con el mejor sazón.",
            direccion = "Calle 123 # 456",
            categoria = "Restaurante",
            horario = mapOf("Lunes-Viernes" to ("08:00" to "20:00")),
            telefono = "3001234567",
            imagenResId = R.drawable.restaurante_mex,
            likes = 25,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "1",
            calificacionPromedio = 4.5,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = emptyList()
        )

        val lugar2 = Lugar(
            id = "l2",
            nombre = "Café La Taza",
            descripcion = "Un lugar acogedor para disfrutar del mejor café.",
            direccion = "Calle 45 # 123",
            categoria = "Café",
            horario = mapOf(
                "Lunes" to ("07:00" to "22:00"),
                "Martes" to ("07:00" to "22:00"),
                "Miércoles" to ("07:00" to "22:00"),
                "Jueves" to ("07:00" to "22:00"),
                "Viernes" to ("07:00" to "22:00"),
                "Sábado" to ("07:00" to "22:00"),
                "Domingo" to ("07:00" to "22:00")
            ),
            telefono = "3019876543",
            imagenResId = R.drawable.cafeteria_moderna,
            likes = 15,
            longitud = -75.56359,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "2",
            calificacionPromedio = 4.7,
            ubicacion = Ubicacion(6.25184, -75.56359),
            comentarios = emptyList()
        )


        _lugares.value = listOf(lugar1, lugar2)
    }

    fun crearLugar(lugar: Lugar) {
        _lugares.value += lugar
    }

    fun buscarPorId(id: String): Lugar? {
        return _lugares.value.find { it.id == id }
    }

    fun buscarPorCategoria(categoria: String): List<Lugar> {
        return _lugares.value.filter { it.categoria.equals(categoria, ignoreCase = true) }
    }

    fun buscarPorCreador(creadorId: String): List<Lugar> {
        return _lugares.value.filter { it.creadorId == creadorId }
    }

    fun actualizarEstado(id: String, nuevoEstado: EstadoLugar) {
        _lugares.value = _lugares.value.map {
            if (it.id == id) it.copy(estado = nuevoEstado) else it
        }
    }

    fun agregarComentario(id: String, comentario: Comentario) {
        _lugares.value = _lugares.value.map {
            if (it.id == id) it.copy(comentarios = it.comentarios + comentario) else it
        }
    }
}
