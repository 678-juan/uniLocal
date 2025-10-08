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
                    texto = "¡Increíble! Fui con mi familia el sábado pasado y quedamos encantados. El sancocho de gallina está para chuparse los dedos 😋. El mesero Carlos nos atendió súper bien y nos explicó cada plato. Definitivamente volveremos!",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 86400000
                ),
                Comentario(
                    id = "c2",
                    usuarioId = "3",
                    lugarId = "l1",
                    texto = "La verdad es que esperaba más por las reseñas, pero no me decepcionó. El arroz con pollo estaba bueno, aunque un poco salado para mi gusto. El ambiente es agradable y los precios están bien. Le doy 4 estrellas porque el servicio fue un poco lento.",
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
                    texto = "¡WOW! Este café es mi lugar favorito para trabajar. El cappuccino con leche de almendras es espectacular ☕. La barista María siempre me saluda con una sonrisa y me conoce mi pedido de memoria. El wifi es súper rápido y el ambiente es perfecto para concentrarse. 100% recomendado!",
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
                    texto = "Llevo 3 meses entrenando aquí y he visto resultados increíbles! 💪 El entrenador Diego me ayudó a crear una rutina personalizada y siempre está pendiente de mi técnica. Los equipos están súper limpios y el ambiente es muy motivador. La única queja es que a veces está muy lleno en las tardes, pero vale la pena!",
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
            comentarios = listOf(
                Comentario(
                    id = "c5",
                    usuarioId = "2",
                    lugarId = "l4",
                    texto = "¡Un paraíso para los amantes de los libros! 📚 Encontré una edición rara de 'Cien años de soledad' que llevaba años buscando. El dueño, don Roberto, es súper conocedor y me recomendó varios títulos que no conocía. El ambiente es muy tranquilo, perfecto para leer. Los precios son justos y tienen descuentos para estudiantes.",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 604800000 // Hace 1 semana
                ),
                Comentario(
                    id = "c6",
                    usuarioId = "5",
                    lugarId = "l4",
                    texto = "Me encanta venir aquí a estudiar. Tienen una sección de libros académicos muy completa y el personal siempre está dispuesto a ayudar. La única cosa es que el aire acondicionado a veces está muy fuerte, pero por lo demás es perfecto. Recomendado para estudiantes universitarios!",
                    estrellas = 4,
                    fecha = System.currentTimeMillis() - 1209600000 // Hace 2 semanas
                )
            )
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
            comentarios = listOf(
                Comentario(
                    id = "c7",
                    usuarioId = "1",
                    lugarId = "l5",
                    texto = "¡Salvadores! 🏥 Tuve una emergencia a las 2 AM y necesitaba medicamentos para mi abuela. Llegué aquí y me atendieron súper rápido. La farmacéutica Laura fue muy amable y me explicó todo sobre los medicamentos. El servicio de entrega a domicilio también es excelente, llegan en menos de 30 minutos.",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 259200000 // Hace 3 días
                ),
                Comentario(
                    id = "c8",
                    usuarioId = "4",
                    lugarId = "l5",
                    texto = "Buen servicio 24 horas, pero los precios son un poco altos comparado con otras farmacias. El personal es amable y tienen buena variedad de medicamentos. La entrega a domicilio funciona bien, aunque a veces se demoran más de lo prometido.",
                    estrellas = 3,
                    fecha = System.currentTimeMillis() - 518400000 // Hace 6 días
                )
            )
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
            comentarios = listOf(
                Comentario(
                    id = "c9",
                    usuarioId = "3",
                    lugarId = "l6",
                    texto = "¡Qué ambiente tan genial! 🍺 Fui con mis amigos el viernes pasado y la pasamos increíble. La banda que tocaba era buenísima, tocaron vallenato y salsa. Los mojitos están deliciosos y el bartender Juan es un crack mezclando. El único detalle es que está un poco caro, pero vale la pena por la experiencia.",
                    estrellas = 4,
                    fecha = System.currentTimeMillis() - 345600000 // Hace 4 días
                ),
                Comentario(
                    id = "c10",
                    usuarioId = "5",
                    lugarId = "l6",
                    texto = "Me encanta este lugar para relajarme después del trabajo. El ambiente es muy acogedor y la música no está tan fuerte como en otros bares. La cerveza está fría y los snacks están buenos. El personal es súper amable, especialmente la mesera Ana que siempre me recuerda mi pedido favorito.",
                    estrellas = 5,
                    fecha = System.currentTimeMillis() - 691200000 // Hace 8 días
                )
            )
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
        val lugar = _lugares.value.find { it.id == id }
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