package com.example.unilocal.ui.pantallas.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.pantallas.admin.navegacionAdmin.ContentAdmin
import androidx.compose.foundation.layout.padding
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeracionViewModel

@Composable
fun PrincipalAdmin(
    moderadorId: String,
    lugaresViewModel: LugaresViewModel? = null
) {
    val navController = rememberNavController()
    // Es bueno usar un val para el ViewModel instanciado para evitar la doble exclamación
    val lugaresViewModelInstance = lugaresViewModel ?: androidx.lifecycle.viewmodel.compose.viewModel<LugaresViewModel>()
    val moderacionViewModel = androidx.lifecycle.viewmodel.compose.viewModel<ModeracionViewModel>()

    // El try-catch ha sido eliminado
    ContentAdmin(
        navController = navController,
        moderadorId = moderadorId,
        lugaresViewModel = lugaresViewModelInstance,
        moderacionViewModel = moderacionViewModel
    )
}