package com.example.unilocal.ui.pantallas.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.pantallas.admin.navegacionAdmin.ContentAdmin
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeradorViewModel


@Composable
fun PrincipalAdmin(
    moderadorId: String,
    lugaresViewModel: LugaresViewModel? = null,
    moderadorViewModel: ModeradorViewModel? = null,
    navegarALogin: () -> Unit = {}
) {
    val navController = rememberNavController()
    // usar val para evitar doble exclamación
    val lugaresViewModelInstance = lugaresViewModel ?: androidx.lifecycle.viewmodel.compose.viewModel<LugaresViewModel>()
    val moderadorViewModelInstance = moderadorViewModel ?: androidx.lifecycle.viewmodel.compose.viewModel<ModeradorViewModel>()

    // ya no necesitamos actualizar lugares en el moderador viewmodel
    // porque ahora usamos estados independientes
    
    ContentAdmin(
        navController = navController,
        moderadorId = moderadorId,
        lugaresViewModel = lugaresViewModelInstance,
        moderadorViewModel = moderadorViewModelInstance,
        navegarALogin = navegarALogin
    )
}