package com.example.unilocal.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TemaApp(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresApp,
        typography = TipografiaApp,
        content = contenido
    )
}