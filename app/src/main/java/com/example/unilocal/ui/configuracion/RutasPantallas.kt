package com.example.unilocal.ui.configuracion

import kotlinx.serialization.Serializable

sealed class RutasPantallas {

    @Serializable
    data object Login : RutasPantallas()

    @Serializable
    data object Registro : RutasPantallas()

    @Serializable
    data object PrincipalUsuarios : RutasPantallas()

    @Serializable
    data object PrincipalAdministrador : RutasPantallas()





}