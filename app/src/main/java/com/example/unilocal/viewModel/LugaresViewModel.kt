package com.example.unilocal.viewModel

import androidx.lifecycle.ViewModel
import com.example.unilocal.model.entidad.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LugaresViewModel : ViewModel() {
    private val _lugares = MutableStateFlow<List<Lugar>>(emptyList())
    val lugares: StateFlow<List<Lugar>> = _lugares.asStateFlow()
    
    // Referencia al UsuarioViewModel para sincronizar likes
    private var usuarioViewModel: UsuarioViewModel? = null
    
    fun setUsuarioViewModel(usuarioViewModel: UsuarioViewModel) {
        this.usuarioViewModel = usuarioViewModel
    }

    init {
        // Cargar lugares de forma asíncrona para no bloquear la UI
        cargarLugares()
    }

    private fun cargarLugares() {
        // Cargar solo lugares esenciales para mejor rendimiento
        val lugar1 = Lugar(
            id = "l1",
            nombre = "Restaurante El Sabor",
            descripcion = "Comida típica colombiana con el mejor sazón.",
            direccion = "Calle 123 # 456",
            categoria = "Restaurante",
            horario = mapOf(
                "Lunes" to ("7:00 AM" to "3:00 PM"),
                "Martes" to ("7:00 AM" to "10:00 PM"),
                "Miércoles" to ("7:00 AM" to "10:00 PM"),
                "Jueves" to ("7:00 AM" to "10:00 PM"),
                "Viernes" to ("7:00 AM" to "10:00 PM"),
                "Sábado" to ("7:00 AM" to "10:00 PM"),
                "Domingo" to ("7:00 AM" to "10:00 PM")
            ),
            telefono = "3001234567",
            imagenUri = "restaurante_mex",
            likes = 25,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "1",
            calificacionPromedio = 4.5,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = listOf(
                Comentario(
                    id = "c1",
                    usuarioId = "2",
                    lugarId = "l1",
                    texto = "Excelente comida y muy buen servicio. La atención es muy amable y los precios son justos.",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 86400000
                ),
                Comentario(
                    id = "c2",
                    usuarioId = "3",
                    lugarId = "l1",
                    texto = "Muy buena experiencia, la comida está deliciosa. Recomiendo el plato especial.",
                    estrellas = 4,
                    fecha = System.currentTimeMillis() - 172800000
                )
            )
        )

        val lugar2 = Lugar(
            id = "l2",
            nombre = "Café La Taza",
            descripcion = "Un lugar acogedor para disfrutar del mejor café.",
            direccion = "Calle 45 # 123",
            categoria = "Café",
            horario = mapOf(
                "Lunes" to ("7:00 AM" to "10:00 PM"),
                "Martes" to ("7:00 AM" to "10:00 PM"),
                "Miércoles" to ("7:00 AM" to "10:00 PM"),
                "Jueves" to ("7:00 AM" to "10:00 PM"),
                "Viernes" to ("7:00 AM" to "10:00 PM"),
                "Sábado" to ("7:00 AM" to "10:00 PM"),
                "Domingo" to ("7:00 AM" to "10:00 PM")
            ),
            telefono = "3019876543",
            imagenUri = "cafeteria_moderna",
            likes = 15,
            longitud = -75.56359,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "2",
            calificacionPromedio = 4.7,
            ubicacion = Ubicacion(6.25184, -75.56359),
            comentarios = listOf(
                Comentario(
                    id = "c3",
                    usuarioId = "1",
                    lugarId = "l2",
                    texto = "El mejor café de la ciudad. Ambiente muy acogedor y el personal es muy amable.",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 259200000
                )
            )
        )

        val lugar3 = Lugar(
            id = "l3",
            nombre = "Gimnasio PowerFit",
            descripcion = "Centro de fitness con equipos modernos y entrenadores certificados.",
            direccion = "Carrera 15 # 78-90",
            categoria = "Gimnasio",
            horario = mapOf(
                "Lunes" to ("5:00 AM" to "10:00 PM"),
                "Martes" to ("5:00 AM" to "10:00 PM"),
                "Miércoles" to ("5:00 AM" to "10:00 PM"),
                "Jueves" to ("5:00 AM" to "10:00 PM"),
                "Viernes" to ("5:00 AM" to "10:00 PM"),
                "Sábado" to ("6:00 AM" to "8:00 PM"),
                "Domingo" to ("6:00 AM" to "8:00 PM")
            ),
            telefono = "3005551234",
            imagenUri = "gimnasio",
            likes = 42,
            longitud = -74.08175,
            estado = EstadoLugar.AUTORIZADO,
            creadorId = "1",
            calificacionPromedio = 4.3,
            ubicacion = Ubicacion(4.60971, -74.08175),
            comentarios = listOf(
                Comentario(
                    id = "c4",
                    usuarioId = "4",
                    lugarId = "l3",
                    texto = "Excelente gimnasio con equipos modernos. Los entrenadores son muy profesionales.",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 432000000 // Hace 5 días
                )
            )
        )

        val lugar4 = Lugar(
            id = "l4",
            nombre = "Librería El Saber",
            descripcion = "Librería especializada en literatura y libros académicos.",
            direccion = "Calle 72 # 11-25",
            categoria = "Librería",
            horario = mapOf(
                "Lunes" to ("9:00 AM" to "7:00 PM"),
                "Martes" to ("9:00 AM" to "7:00 PM"),
                "Miércoles" to ("9:00 AM" to "7:00 PM"),
                "Jueves" to ("9:00 AM" to "7:00 PM"),
                "Viernes" to ("9:00 AM" to "7:00 PM"),
                "Sábado" to ("9:00 AM" to "7:00 PM"),
                "Domingo" to ("10:00 AM" to "4:00 PM")
            ),
            telefono = "3007778888",
            imagenUri = "libreria",
            likes = 18,
            longitud = -74.08175,
            estado = EstadoLugar.PENDIENTE,
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
                "Lunes" to ("12:00 AM" to "11:59 PM"),
                "Martes" to ("12:00 AM" to "11:59 PM"),
                "Miércoles" to ("12:00 AM" to "11:59 PM"),
                "Jueves" to ("12:00 AM" to "11:59 PM"),
                "Viernes" to ("12:00 AM" to "11:59 PM"),
                "Sábado" to ("12:00 AM" to "11:59 PM"),
                "Domingo" to ("12:00 AM" to "11:59 PM")
            ),
            telefono = "3009990000",
            imagenUri = "farmacia",
            likes = 35,
            longitud = -74.08175,
            estado = EstadoLugar.PENDIENTE,
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
                "Lunes" to ("Cerrado" to "Cerrado"),
                "Martes" to ("6:00 PM" to "2:00 AM"),
                "Miércoles" to ("6:00 PM" to "2:00 AM"),
                "Jueves" to ("6:00 PM" to "2:00 AM"),
                "Viernes" to ("6:00 PM" to "2:00 AM"),
                "Sábado" to ("6:00 PM" to "2:00 AM"),
                "Domingo" to ("6:00 PM" to "2:00 AM")
            ),
            telefono = "3001112222",
            imagenUri = "bar",
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

    fun borrarLugar(lugarId: String) {
        _lugares.value = _lugares.value.filter { it.id != lugarId }
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
        _lugares.value = _lugares.value.map { lugar ->
            if (lugar.id == lugarId) {
                lugar.copy(comentarios = lugar.comentarios + comentario)
            } else {
                lugar
            }
        }
    }

    fun darLike(lugarId: String) {
        // Solo actualizar el contador de likes en el lugar
        _lugares.value = _lugares.value.map { lugar ->
            if (lugar.id == lugarId) {
                lugar.copy(likes = lugar.likes + 1)
            } else {
                lugar
            }
        }
        
        // Delegar la gestión de likes del usuario al UsuarioViewModel
        usuarioViewModel?.darLike(lugarId)
    }

    fun quitarLike(lugarId: String) {
        // Solo actualizar el contador de likes en el lugar
        _lugares.value = _lugares.value.map { lugar ->
            if (lugar.id == lugarId) {
                lugar.copy(likes = maxOf(0, lugar.likes - 1))
            } else {
                lugar
            }
        }
        
        // Delegar la gestión de likes del usuario al UsuarioViewModel
        usuarioViewModel?.quitarLike(lugarId)
    }

    fun yaDioLike(lugarId: String): Boolean {
        // Delegar la verificación al UsuarioViewModel
        return usuarioViewModel?.yaDioLike(lugarId) ?: false
    }

    fun agregarFavorito(lugarId: String) {
        // Esta función se maneja en UsuarioViewModel
    }

    fun quitarFavorito(lugarId: String) {
        // Esta función se maneja en UsuarioViewModel
    }

    fun estaGuardado(lugarId: String): Boolean {
        // Esta función se maneja en UsuarioViewModel
        return false
    }
    
    fun responderComentario(lugarId: String, comentarioId: String, respuesta: String) {
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
    }

}