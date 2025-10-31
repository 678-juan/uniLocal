package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.unilocal.R
import com.example.unilocal.model.entidad.Lugar
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.remember

@Composable
fun FichaLugarPerfil(
    lugar: Lugar,
    onClick: () -> Unit,
    onBorrar: () -> Unit,
    onVerComentarios: () -> Unit = {}
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // imagen del lugar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
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
                            "pizzeria_roma" -> R.drawable.pizzeria_roma
                            "burger_house" -> R.drawable.burger_house
                            else -> R.drawable.logo
                        }),
                        contentDescription = lugar.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // información del lugar
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lugar.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                
                Text(
                    text = lugar.categoria,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Text(
                    text = lugar.direccion,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // botones de acción
            Row {
                // botón de comentarios
                IconButton(
                    onClick = onVerComentarios
                ) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Ver comentarios",
                        tint = Color(0xFF2196F3)
                    )
                }
                
                // botón de borrar
                IconButton(
                    onClick = { mostrarDialogo = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar lugar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
    
    // diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Borrar lugar") },
            text = { Text("¿Estás seguro de que quieres borrar este lugar? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBorrar()
                        mostrarDialogo = false
                    }
                ) {
                    Text("Borrar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
