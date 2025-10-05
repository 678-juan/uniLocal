package com.example.unilocal.ui.pantallas.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.unilocal.ui.pantallas.admin.navegacionAdmin.ContentAdmin
import androidx.compose.foundation.layout.padding

@Composable
fun PrincipalAdmin(moderadorId: String) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { /* La barra se dibuja dentro de ContentAdmin */ }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            ContentAdmin(navController = navController, moderadorId = moderadorId)
        }
    }
}
