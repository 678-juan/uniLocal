package com.example.unilocal.model.entidad

data class Lugar(
    val id: String ,
    val nombre: String ,
    val descripcion: String ,
    val categoria: String ,
    val horario: Map<String, Pair<String, String>> = emptyMap(),
    val telefono: String ,
    val imagenes: List<String> = emptyList(),
    val latitud: Double ,
    val longitud: Double ,
    val estado: EstadoLugar = EstadoLugar.PENDIENTE,
    val creadorId: String ,
    val calificacionPromedio: Double
)