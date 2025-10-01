package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.example.unilocal.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unilocal.ui.componentes.FichaLugar


@Composable
fun Recomendaciones() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // busqueda basica
        OutlinedTextField(
            value = "Desciada das escadas de Santos",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tu ubicación", fontWeight = FontWeight.Bold)

        // imagen mapa
        Image(
            painter = painterResource(id = R.drawable.mapamomentaneo), // Usa tu imagen del mapa
            contentDescription = "Mapa",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recomendaciones cerca a tu ubicación", fontWeight = FontWeight.Bold)

        // poner unna lista preparada de recomendaciones
        Column {
            FichaLugar(
                imagenUrl = "https://picsum.photos/400/200",
                titulo = "Gourmet Express",
                descripcion = "Cocina internacional"
            )
            FichaLugar(
                imagenUrl = "https://picsum.photos/401/200",
                titulo = "Comida Italiana",
                descripcion = "Pasta & Pizza"
            )
            FichaLugar(
                imagenUrl = "https://picsum.photos/402/200",
                titulo = "Cafetería - Hotel",
                descripcion = "Ambiente acogedor"
            )
        }
    }
}
