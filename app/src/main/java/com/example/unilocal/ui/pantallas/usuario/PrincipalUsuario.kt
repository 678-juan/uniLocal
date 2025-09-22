package com.example.unilocal.ui.pantallas.usuario

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.unilocal.R


@Composable
fun PrincipalUsuario() {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarUsuario()
        },

        bottomBar = {
            BottonBarUsuario()
        }

    ) { padding ->
        ContentUsuario(
            padding = padding
        )

    }
}

@Composable
fun ContentUsuario(
    padding: PaddingValues
) {

}
@Composable
fun TopBarUsuario() {

}

@Composable
fun BottonBarUsuario() {

    val startDestinon = Destino.INICIO
    var selectedDestino by rememberSaveable {mutableStateOf(startDestinon.ordinal)}
    NavigationBar(

    ){
        Destino.entries.forEachIndexed { index, destino ->
            NavigationBarItem(
                onClick =  {
                    selectedDestino = index
                },
                icon = {
                    Icon(
                        imageVector = destino.icono,
                        contentDescription = stringResource(destino.texto)
                    )
                },
                selected = selectedDestino == index,
            )
        }

    }



}

enum class Destino(
    val ruta: String,
    val texto: Int,
    val icono: ImageVector

){
    INICIO("",R.string.inicio,Icons.Default.Home),
    BUSQUEDA("",R.string.busqueda,Icons.Default.Search),
    CREARLUGAR("",R.string.crear_lugar,Icons.Default.AddCircle),
    RECOMENDACIONES("",R.string.recomendados,Icons.Default.Place),
    PERFIL("",R.string.perfil,Icons.Default.Person)
}
