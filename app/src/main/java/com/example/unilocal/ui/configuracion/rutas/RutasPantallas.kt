package com.example.unilocal.ui.configuracion.rutas

import kotlinx.serialization.Serializable

sealed class RutasPantallas {

    @Serializable
    data object Login : RutasPantallas()

    @Serializable
    data object Registro : RutasPantallas()
}