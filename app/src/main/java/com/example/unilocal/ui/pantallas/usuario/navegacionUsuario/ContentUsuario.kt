package com.example.unilocal.ui.pantallas.usuario.navegacionUsuario

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.unilocal.ui.pantallas.usuario.PantallaEditarUsuario
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Busqueda
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.CrearLugar
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Inicio
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Perfil
import com.example.unilocal.ui.pantallas.usuario.tapsUsuario.Recomendaciones

@Composable
fun ContentUsuario(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = RutaTab.InicioUsuario
    ){
        composable<RutaTab.InicioUsuario> {
            Inicio()
        }
        composable<RutaTab.Busqueda> {
            Busqueda()
        }
        composable<RutaTab.CrearLugar> {
            CrearLugar()
        }
        composable<RutaTab.Recomendados> {
            Recomendaciones()
        }
        composable<RutaTab.Perfil> {
            Perfil(
                navegarACrearLugar = {
                    navController.navigate(RutaTab.CrearLugar)
                }
            )
        }
    }

}