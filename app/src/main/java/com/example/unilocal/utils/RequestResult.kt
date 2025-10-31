package com.example.unilocal.utils

sealed class RequestResult {
    data class Sucess(val mensaje: String) : RequestResult()
    data class Error(val errorMensaje: String) : RequestResult()
    object Cargar : RequestResult()
}


