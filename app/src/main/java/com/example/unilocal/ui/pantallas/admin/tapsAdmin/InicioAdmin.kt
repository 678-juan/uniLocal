package com.example.unilocal.ui.pantallas.admin.tapsAdmin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeradorViewModel
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.ui.componentes.FichaLugarAdmin


import androidx.compose.ui.draw.clip

import androidx.compose.ui.platform.LocalContext

import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.unilocal.R
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.remember


@Composable
fun InicioAdmin(
    moderadorId: String,
    lugaresViewModel: LugaresViewModel = viewModel(),
    moderadorViewModel: ModeradorViewModel = viewModel()
) {
    val lugares = lugaresViewModel.lugares.collectAsState().value
    val pendientes = lugares.filter { it.estado == EstadoLugar.PENDIENTE }
    val autorizadosPorMi = moderadorViewModel.misAutorizados.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        // perfil moderador
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil),
                    contentDescription = "Moderador",
                    modifier = Modifier.size(56.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(text = "Panel de moderación", style = MaterialTheme.typography.titleMedium)
                    Text(text = "ID: $moderadorId", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                // métricas
                AssistChip(onClick = {}, label = { Text("Pendientes ${pendientes.size}") })
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(text = "Solicitudes pendientes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        pendientes.take(3).forEach { lugar ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.Black, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // imagen pequeña del lugar
                    if (lugar.imagenUri != "default_image") {
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
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = lugar.nombre,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
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
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape),
                                contentScale = ContentScale.Crop
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
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                    }

                    // información del lugar
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = lugar.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = lugar.categoria,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = lugar.descripcion,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    // botones de acción compactos
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = {
                                lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.AUTORIZADO)
                                moderadorViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.AUTORIZADO)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.size(width = 80.dp, height = 32.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text("✅", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.RECHAZADO)
                                moderadorViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.RECHAZADO)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            modifier = Modifier.size(width = 80.dp, height = 32.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text("❌", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // sección de lugares autorizados
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lugares Autorizados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                "${autorizadosPorMi.size} lugares",
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (autorizadosPorMi.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no has autorizado lugares",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    // primeros 3 lugares
                    autorizadosPorMi.take(3).forEach { lugar ->
                        FichaLugarAdmin(
                            lugar = lugar,
                            estado = "Autorizado",
                            onClick = { /* navegar a detalles si es necesario */ }
                        )
                    }

                    // ver más
                    if (autorizadosPorMi.size > 3) {
                        Text(
                            text = "... y ${autorizadosPorMi.size - 3} lugares más",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

