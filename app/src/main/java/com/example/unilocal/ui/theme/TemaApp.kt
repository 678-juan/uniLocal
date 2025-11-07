package com.example.unilocal.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable

@Composable
fun TemaApp(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresApp,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

