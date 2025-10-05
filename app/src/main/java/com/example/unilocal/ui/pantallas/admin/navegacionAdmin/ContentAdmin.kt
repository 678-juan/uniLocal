package com.example.unilocal.ui.pantallas.admin.navegacionAdmin

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.unilocal.ui.pantallas.admin.tapsAdmin.HistorialAdmin
import com.example.unilocal.ui.pantallas.admin.tapsAdmin.InicioAdmin
import com.example.unilocal.ui.pantallas.admin.tapsAdmin.SolicitudesAdmin

sealed class RutaAdmin(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Inicio: RutaAdmin("admin/inicio", "Inicio", androidx.compose.material.icons.Icons.Default.Home)
    object Solicitudes: RutaAdmin("admin/solicitudes", "Solicitudes", androidx.compose.material.icons.Icons.Default.List)
    object Historial: RutaAdmin("admin/historial", "Historial", androidx.compose.material.icons.Icons.Default.History)
}

@Composable
fun ContentAdmin(navController: NavHostController, moderadorId: String) {
    val tabs = listOf(RutaAdmin.Inicio, RutaAdmin.Solicitudes, RutaAdmin.Historial)
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavHost(navController = navController, startDestination = RutaAdmin.Inicio.route) {
        composable(RutaAdmin.Inicio.route) { InicioAdmin(moderadorId = moderadorId) }
        composable(RutaAdmin.Solicitudes.route) { SolicitudesAdmin(moderadorId = moderadorId) }
        composable(RutaAdmin.Historial.route) { HistorialAdmin() }
    }

    NavigationBar {
        tabs.forEach { tab ->
            val selected = backStackEntry?.destination?.route == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(tab.route) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) }
            )
        }
    }
}
