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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun Recomendaciones() {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState) // <-- scroll aquí
    ) {
        Text("Tu ubicación", fontWeight = FontWeight.Bold)

        // imagen mapa
        Image(
            painter = painterResource(id = R.drawable.mapamomentaneo),
            contentDescription = "Mapa",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recomendaciones cerca a tu ubicación", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // poner una lista preparada de recomendaciones
        Column {
            FichaLugar(
                imagenResId = R.drawable.restaurante_mex,
                titulo = "El Sombrero",
                descripcion = "Comida mexicana auténtica"
            )
            FichaLugar(
                imagenResId = R.drawable.cafeteria_moderna,
                titulo = "Café Modernista",
                descripcion = "Café de especialidad y postres"
            )
            FichaLugar(
                imagenResId = R.drawable.pizzeria_roma,
                titulo = "Pizzería La Roma",
                descripcion = "Pizza artesanal y horno de leña"
            )
            FichaLugar(
                imagenResId = R.drawable.burger_house,
                titulo = "Burger House",
                descripcion = "Hamburguesas gourmet y papas rústicas"
            )

        }
    }
}
