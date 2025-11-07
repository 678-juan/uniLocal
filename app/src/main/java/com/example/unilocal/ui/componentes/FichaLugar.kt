package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.unilocal.viewModel.LugaresViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.BitmapFactory
import android.util.Base64


@Composable
fun FichaLugar(
    lugar: Lugar,
    onClick: () -> Unit,
    lugaresViewModel: LugaresViewModel? = null,
    modifier: Modifier = Modifier
) {
    val lugaresVM: LugaresViewModel = lugaresViewModel ?: viewModel()

    // Memoizar cálculos costosos
    val estaAbierto = remember(lugar.id) { lugaresVM.estaAbierto(lugar) }

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
            if (lugar.imagenUri.startsWith("data:image")) {
                // Base64 - decodificar y mostrar
                val base64String = lugar.imagenUri.substringAfter(",")
                val bitmap = remember(base64String) {
                    try {
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = lugar.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = lugar.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (
                lugar.imagenUri.startsWith("content://") ||
                lugar.imagenUri.startsWith("file://") ||
                lugar.imagenUri.startsWith("http://") ||
                lugar.imagenUri.startsWith("https://")
            ) {
                // URI o URL - usar Coil
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
            // Estado abierto/cerrado en la esquina superior derecha
            AssistChip(
                onClick = {
                    // Prueba temporal
                    println("=== PRUEBA ESTADO ===")
                    println("Lugar: ${lugar.nombre}")
                    println("¿Abierto? $estaAbierto")
                },
                label = {
                    Text(
                        text = if (estaAbierto) "Abierto" else "Cerrado",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (estaAbierto) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                )
            )

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

