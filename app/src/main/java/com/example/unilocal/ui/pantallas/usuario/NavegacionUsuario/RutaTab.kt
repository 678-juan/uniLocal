package com.example.unilocal.ui.pantallas.usuario.NavegacionUsuario

import kotlinx.serialization.Serializable

sealed class RutaTab() {

    @Serializable
    data object Busqueda : RutaTab()

    @Serializable
    data object CrearLugar : RutaTab()

    @Serializable
    data object Inicio : RutaTab()

    @Serializable
    data object Perfil : RutaTab()

    @Serializable
    data object Recomendados : RutaTab()

}