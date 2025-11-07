package com.example.unilocal.ui.pantallas.usuario.navegacionUsuario

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.unilocal.ui.pantallas.usuario.PantallaEditarUsuario
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Busqueda
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.CambiarPassword
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Configuracion
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.CrearLugar
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Guardados
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Inicio
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.LugarDetalles
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Perfil
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Recomendaciones
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel

@Composable
fun ContentUsuario(
    navController: NavHostController,
    padding: PaddingValues,
    usuarioViewModel: UsuarioViewModel? = null,
    lugaresViewModel: LugaresViewModel? = null,
    navegarALogin: () -> Unit = {}
) {
    // Usar los ViewModels pasados como parámetros para mantener consistencia
    val lugaresViewModelInstance = lugaresViewModel ?: throw IllegalStateException("LugaresViewModel es requerido")
    val viewModel = usuarioViewModel ?: throw IllegalStateException("UsuarioViewModel es requerido")

    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = RutaTab.InicioUsuario
    ){
        composable<RutaTab.InicioUsuario> {
            Inicio(
                lugaresViewModel = lugaresViewModelInstance,
                usuarioViewModel = viewModel,
                navegarALugar = { lugarId ->
                    navController.navigate(RutaTab.LugarDetalles(lugarId))
                }
            )
        }
        composable<RutaTab.Busqueda> {
            Busqueda(
                navController = navController,
                lugaresViewModel = lugaresViewModelInstance
            )
        }
        composable<RutaTab.CrearLugar> {
            CrearLugar(
                navController = navController,
                usuarioViewModel = viewModel,
                lugaresViewModel = lugaresViewModelInstance
            )
        }
        composable<RutaTab.Recomendados> {
            Recomendaciones(
                lugaresViewModel = lugaresViewModelInstance,
                navegarALugar = {
                    navController.navigate(RutaTab.LugarDetalles(it))
                },
                usuarioViewModel = viewModel
            )
        }
        composable<RutaTab.Perfil> {
            Perfil(
                navegarACrearLugar = {
                    navController.navigate(RutaTab.CrearLugar)
                },
                usuarioViewModel = viewModel,
                lugaresViewModel = lugaresViewModelInstance,
                navegarALugar = { lugarId ->
                    navController.navigate(RutaTab.LugarDetalles(lugarId))
                },
                navegarAConfiguracion = {
                    navController.navigate(RutaTab.Configuracion)
                }
            )
        }

        composable<RutaTab.LugarDetalles> {
            val args = it.toRoute<RutaTab.LugarDetalles>()

            LugarDetalles(
                idLugar = args.idLugar,
                navController = navController,
                usuarioViewModel = viewModel,
                lugaresViewModel = lugaresViewModelInstance
            )
        }

        composable<RutaTab.Configuracion> {
            Configuracion(
                navController = navController,
                usuarioViewModel = viewModel,
                navegarALogin = navegarALogin
            )
        }

        composable<RutaTab.Guardados> {
            Guardados(
                navController = navController,
                usuarioViewModel = viewModel
            )
        }

        composable<RutaTab.CambiarPassword> {
            CambiarPassword(
                navController = navController,
                usuarioViewModel = viewModel
            )
        }

        composable<RutaTab.EditarUsuario> {
            PantallaEditarUsuario(
                navController = navController,
                usuarioViewModel = viewModel
            )
        }

    }

}

