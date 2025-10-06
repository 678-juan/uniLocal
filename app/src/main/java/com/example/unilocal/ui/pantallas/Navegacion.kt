package com.example.unilocal.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.configuracion.RutasPantallas
import com.example.unilocal.ui.pantallas.usuario.PrincipalUsuario
import com.example.unilocal.ui.pantallas.admin.PrincipalAdmin
import com.example.unilocal.viewModel.UsuarioViewModel
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeradorViewModel


@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val lugaresViewModel: LugaresViewModel = viewModel()
    val moderadorViewModel: ModeradorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = RutasPantallas.Login
    ) {
        composable<RutasPantallas.Login> {
            PantallaLogin(
                navegarAPrincipalUsuario = {
                    navController.navigate(RutasPantallas.PrincipalUsuarios)
                },
                navegarAPrincipalAdmin = { moderadorId ->
                    navController.navigate(RutasPantallas.PrincipalAdministrador)
                },
                navegarARegistro = {
                    navController.navigate(RutasPantallas.Registro)
                },
                usuarioViewModel = usuarioViewModel,
                moderadorViewModel = moderadorViewModel
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
            PrincipalUsuario(
                usuarioViewModel = usuarioViewModel, 
                lugaresViewModel = lugaresViewModel,
                navegarALogin = {
                    navController.navigate(RutasPantallas.Login) {
                        popUpTo(RutasPantallas.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<RutasPantallas.PrincipalAdministrador> {
            PrincipalAdmin(
                moderadorId = "mod1", 
                lugaresViewModel = lugaresViewModel,
                moderadorViewModel = moderadorViewModel,
                navegarALogin = {
                    navController.navigate(RutasPantallas.Login) {
                        popUpTo(RutasPantallas.Login) { inclusive = true }
                    }
                }
            )
        }





    }
}