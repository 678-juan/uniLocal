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

        val lugar3 = Lugar(
            id = "l3",
            nombre = "Gimnasio PowerFit",
            descripcion = "Centro de fitness con equipos modernos y entrenadores certificados.",
            direccion = "Carrera 15 # 78-90",
            categoria = "Gimnasio",
            horario = mapOf(
                "Lunes-Viernes" to ("05:00" to "22:00"),
                "Sábado-Domingo" to ("06:00" to "20:00")
            ),
            telefono = "3005551234",
            imagenResId = R.drawable.gimnasio,
            likes = 42,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "1",
            calificacionPromedio = 4.3,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = emptyList()
        )

        val lugar4 = Lugar(
            id = "l4",
            nombre = "Librería El Saber",
            descripcion = "Librería especializada en literatura y libros académicos.",
            direccion = "Calle 72 # 11-25",
            categoria = "Librería",
            horario = mapOf(
                "Lunes-Sábado" to ("09:00" to "19:00"),
                "Domingo" to ("10:00" to "16:00")
            ),
            telefono = "3007778888",
            imagenResId = R.drawable.libreria,
            likes = 18,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "3",
            calificacionPromedio = 4.6,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = emptyList()
        )

        val lugar5 = Lugar(
            id = "l5",
            nombre = "Farmacia Salud Total",
            descripcion = "Farmacia 24 horas con servicio de entrega a domicilio.",
            direccion = "Carrera 7 # 32-16",
            categoria = "Farmacia",
            horario = mapOf(
                "Lunes-Domingo" to ("00:00" to "23:59")
            ),
            telefono = "3009990000",
            imagenResId = R.drawable.farmacia,
            likes = 35,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "2",
            calificacionPromedio = 4.4,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = emptyList()
        )

        val lugar6 = Lugar(
            id = "l6",
            nombre = "Bar El Rincón",
            descripcion = "Bar tradicional con música en vivo y ambiente relajado.",
            direccion = "Calle 85 # 12-45",
            categoria = "Bar",
            horario = mapOf(
                "Martes-Domingo" to ("18:00" to "02:00"),
                "Lunes" to ("Cerrado" to "Cerrado")
            ),

            telefono = "3001112222",
            imagenResId = R.drawable.bar,
            likes = 28,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "1",
            calificacionPromedio = 4.2,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = emptyList()
        )

        _lugares.value = listOf(lugar1, lugar2, lugar3, lugar4, lugar5, lugar6)
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
