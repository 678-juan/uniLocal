package com.example.unilocal.ui.pantallas.usuario

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.R
import com.example.unilocal.ui.pantallas.usuario.NavegacionUsuario.RutaTab
import com.example.unilocal.ui.pantallas.usuario.Taps.Busqueda
import com.example.unilocal.ui.pantallas.usuario.Taps.CrearLugar
import com.example.unilocal.ui.pantallas.usuario.Taps.Inicio
import com.example.unilocal.ui.pantallas.usuario.Taps.Perfil
import com.example.unilocal.ui.pantallas.usuario.Taps.Recomendaciones


@Composable
fun PrincipalUsuario() {

    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarUsuario()
        },

        bottomBar = {
            BottonBarUsuario(
                navController = navController
            )
        }

    ) { padding ->
        ContentUsuario(
            navController = navController,
            padding = padding
        )

    }
}

@Composable
fun ContentUsuario(
    navController: NavHostController,
    padding: PaddingValues
) {


    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = RutaTab.Inicio
    ){
        composable<RutaTab.Inicio> {
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
            Perfil()
        }
    }

}
@Composable
fun TopBarUsuario() {

}

@Composable
fun BottonBarUsuario(
    navController: NavHostController,

) {

    val startDestinon = Destino.INICIO
    var selectedDestino by rememberSaveable {mutableStateOf(startDestinon.ordinal)}
    NavigationBar(
        modifier = Modifier

            .height(100.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ){
        Destino.entries.forEachIndexed { index, destino ->
            NavigationBarItem(
                onClick =  {

                    selectedDestino = index
                    navController.navigate(destino.ruta)


                },
                icon = {
                    Icon(
                        imageVector = destino.icono,
                        contentDescription = stringResource(destino.texto)
                    )
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                selected = selectedDestino == index,
            )
        }

    }



}

enum class Destino(
    val ruta: RutaTab,
    val texto: Int,
    val icono: ImageVector

){
    INICIO(RutaTab.Inicio,R.string.inicio,Icons.Default.Home),
    BUSQUEDA(RutaTab.Busqueda,R.string.busqueda,Icons.Default.Search),
    CREARLUGAR(RutaTab.CrearLugar,R.string.crear_lugar,Icons.Default.AddCircle),
    RECOMENDACIONES(RutaTab.Recomendados,R.string.recomendados,Icons.Default.Place),
    PERFIL(RutaTab.Perfil,R.string.perfil,Icons.Default.Person)
}
