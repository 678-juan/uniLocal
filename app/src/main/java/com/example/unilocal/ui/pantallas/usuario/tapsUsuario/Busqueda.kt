package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.CampoMinimalista
import com.example.unilocal.ui.componentes.FichaLugar

@Composable
fun Busqueda() {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CampoMinimalista(
            value = text,
            onValueChange = { text = it },
            placeholder = "Busca el lugar adecuado"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recomendaciones mágicas", fontWeight = FontWeight.Bold)

        FichaLugar(
            imagenResId = R.drawable.cafeteria_moderna,
            titulo = "Café Modernista",
            descripcion = "Café de especialidad y postres"
        )
    }
}

