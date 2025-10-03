package com.example.unilocal.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.configuracion.RutasPantallas
import com.example.unilocal.ui.pantallas.usuario.PrincipalUsuario
import com.example.unilocal.viewModel.UsuarioViewModel


@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = RutasPantallas.Login
    ) {
        composable<RutasPantallas.Login> {
            PantallaLogin(
                navegarAPrincipalUsuario = {
                    navController.navigate(RutasPantallas.PrincipalUsuarios)
                },
                navegarARegistro = {
                    navController.navigate(RutasPantallas.Registro)
                },
                usuarioViewModel = usuarioViewModel
            )
        }
        composable<RutasPantallas.Registro> {
            PantallaRegisto(
                navegarALogin = {
                    navController.navigate(RutasPantallas.Login)
                },
                usuarioViewModel = usuarioViewModel
            )
        }

        composable<RutasPantallas.PrincipalUsuarios> {
            PrincipalUsuario(usuarioViewModel = usuarioViewModel)
        }





    }
}