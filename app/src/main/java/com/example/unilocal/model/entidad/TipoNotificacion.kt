package com.example.unilocal.model.entidad

import kotlinx.serialization.Serializable

@Serializable
enum class TipoNotificacion {
    LUGAR_AUTORIZADO,
    LUGAR_RECHAZADO,
    COMENTARIO_NUEVO,
    LUGAR_FAVORITO
}

