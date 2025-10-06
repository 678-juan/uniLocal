package com.example.unilocal.ui.pantallas.admin.navegacionAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.unilocal.ui.pantallas.admin.tapsAdmin.HistorialAdmin
import com.example.unilocal.ui.pantallas.admin.tapsAdmin.InicioAdmin
import com.example.unilocal.ui.pantallas.admin.tapsAdmin.SolicitudesAdmin
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeradorViewModel

sealed class RutaAdmin(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Inicio: RutaAdmin("admin/inicio", "Inicio", androidx.compose.material.icons.Icons.Default.Home)
    object Solicitudes: RutaAdmin("admin/solicitudes", "Solicitudes", androidx.compose.material.icons.Icons.Default.List)
    object Historial: RutaAdmin("admin/historial", "Historial", androidx.compose.material.icons.Icons.Default.History)
}

@Composable
fun ContentAdmin(
    navController: NavHostController,
    moderadorId: String,
    lugaresViewModel: LugaresViewModel = viewModel(),
    moderadorViewModel: ModeradorViewModel = viewModel(),
    navegarALogin: () -> Unit = {}
) {
    // Se ha eliminado el bloque 'try-catch' que causaba el error.

    val tabs = listOf(RutaAdmin.Inicio, RutaAdmin.Solicitudes, RutaAdmin.Historial)
    val backStackEntry by navController.currentBackStackEntryAsState()

    androidx.compose.material3.Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
                containerColor = Color.White
            ) {
                tabs.forEach { tab ->
                    val selected = backStackEntry?.destination?.route == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigate(tab.route) },
                        icon = { 
                            Icon(
                                tab.icon, 
                                contentDescription = tab.label,
                                tint = Color.Black
                            ) 
                        },
                        label = { 
                            Text(
                                tab.label,
                                color = Color.Black
                            ) 
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = RutaAdmin.Inicio.route,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(RutaAdmin.Inicio.route) {
                InicioAdmin(
                    moderadorId = moderadorId,
                    lugaresViewModel = lugaresViewModel,
                    moderadorViewModel = moderadorViewModel
                )
            }
            composable(RutaAdmin.Solicitudes.route) {
                SolicitudesAdmin(
                    moderadorId = moderadorId,
                    lugaresViewModel = lugaresViewModel,
                    moderadorViewModel = moderadorViewModel
                )
            }
            composable(RutaAdmin.Historial.route) {
                HistorialAdmin(
                    moderadorViewModel = moderadorViewModel,
                    lugaresViewModel = lugaresViewModel,
                    navegarALogin = navegarALogin
                )
            }
        }
    }
}