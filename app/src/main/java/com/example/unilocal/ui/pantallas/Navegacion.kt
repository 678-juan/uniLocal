package com.example.unilocal.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.configuracion.rutas.RutasPantallas

@Composable
fun Navegacion(){

    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = RutasPantallas.Login)
    {

    }