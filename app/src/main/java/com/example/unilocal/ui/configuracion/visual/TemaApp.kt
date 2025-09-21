package com.example.unilocal.ui.configuracion.visual


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.unilocal.ui.theme.ColoresApp

@Composable
fun TemaApp(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresApp,
        typography = TipografiaApp,
        content = contenido
    )
}