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
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Busqueda
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.CrearLugar
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
    usuarioViewModel: UsuarioViewModel? = null
) {
    val lugaresViewModel:LugaresViewModel = viewModel()
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()

    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = RutaTab.InicioUsuario
    ){
        composable<RutaTab.InicioUsuario> {
            Inicio(
                lugaresViewModel = lugaresViewModel,
                usuarioViewModel = viewModel,
                navegarALugar = { lugarId ->
                    println("DEBUG: Navegando a detalles del lugar: $lugarId")
                    navController.navigate(RutaTab.LugarDetalles(lugarId))
                }
            )
        }
        composable<RutaTab.Busqueda> {
            Busqueda(navController = navController)
        }
        composable<RutaTab.CrearLugar> {
            CrearLugar()
        }
        composable<RutaTab.Recomendados> {
            Recomendaciones(
                lugaresViewModel = lugaresViewModel,
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
                lugaresViewModel = lugaresViewModel,
                navegarALugar = { lugarId ->
                    navController.navigate(RutaTab.LugarDetalles(lugarId))
                }
            )
        }

        composable<RutaTab.LugarDetalles> {
            val args = it.toRoute<RutaTab.LugarDetalles>()

            LugarDetalles(
                idLugar = args.idLugar,
                navController = navController,
                usuarioViewModel = viewModel
            )
        }

    }

}