package com.example.unilocal.model.entidad

import kotlinx.serialization.Serializable
@Serializable
enum class EstadoLugar {
    PENDIENTE,
    AUTORIZADO,
    RECHAZADO
}

