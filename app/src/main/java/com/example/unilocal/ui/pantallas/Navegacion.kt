package com.example.unilocal.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.configuracion.RutasPantallas


@Composable
fun Navegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasPantallas.Login
    ) {
        composable<RutasPantallas.Login> {
            PantallaLogin(
                navegarARegistro = {
                    navController.navigate(RutasPantallas.Registro)
                }
            )
        }
        composable<RutasPantallas.Registro> {
            PantallaRegisto()
        }
    }
}