package com.example.unilocal.ui.pantallas.usuario.bottonBarUsuario

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.unilocal.ui.pantallas.usuario.Destino


@Composable
fun BottonBarUsuario(
    navController: NavHostController,

    ) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val topRounded = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    NavigationBar(
        modifier = Modifier

            .height(100.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = topRounded
            )
            .clip(topRounded),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ){
        Destino.entries.forEachIndexed { index, destino ->

            val isSelected = currentDestination?.route == destino.ruta::class.qualifiedName
            NavigationBarItem(
                onClick =  {


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
                selected = isSelected,
            )
        }

    }



}