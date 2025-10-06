package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.unilocal.model.entidad.Lugar
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.unilocal.R


@Composable
fun FichaLugar(
    lugar: Lugar,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { 
                println("DEBUG: Click en FichaLugar - ${lugar.nombre}")
                onClick() 
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            if (lugar.imagenUri.startsWith("content://") || lugar.imagenUri.startsWith("file://")) {
                // uri de imagen seleccionada
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(lugar.imagenUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = lugar.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // recurso drawable
                Image(
                    painter = painterResource(id = when (lugar.imagenUri) {
                        "restaurante_mex" -> R.drawable.restaurante_mex
                        "cafeteria_moderna" -> R.drawable.cafeteria_moderna
                        "gimnasio" -> R.drawable.gimnasio
                        "libreria" -> R.drawable.libreria
                        "farmacia" -> R.drawable.farmacia
                        "bar" -> R.drawable.bar
                        else -> R.drawable.logo
                    }),
                    contentDescription = lugar.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = lugar.nombre,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
