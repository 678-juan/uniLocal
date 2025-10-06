package com.example.unilocal.ui.pantallas.admin.tapsAdmin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeracionViewModel
import com.example.unilocal.model.entidad.EstadoLugar

@Composable
fun SolicitudesAdmin(
    moderadorId: String,
    lugaresViewModel: LugaresViewModel = viewModel(),
    moderacionViewModel: ModeracionViewModel = viewModel()
) {
    val pendientes = lugaresViewModel.lugares.collectAsState().value
        .filter { it.estado == EstadoLugar.PENDIENTE }

    // Debug: mostrar información
    LaunchedEffect(pendientes.size) {
        println("DEBUG: Lugares pendientes encontrados: ${pendientes.size}")
        pendientes.forEach { lugar ->
            println("DEBUG: Lugar pendiente: ${lugar.nombre} - Estado: ${lugar.estado}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Solicitudes (${pendientes.size})", 
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))

        if (pendientes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📋 No hay solicitudes pendientes",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Los lugares creados por usuarios aparecerán aquí para moderación",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            pendientes.forEach { lugar ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column {
                    // Imagen del lugar
                    if (lugar.imagenResId != 0) {
                        Image(
                            painter = painterResource(id = lugar.imagenResId),
                            contentDescription = lugar.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Contenido de la tarjeta
                    Column(Modifier.padding(16.dp)) {
                        // Header con nombre y categoría
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = lugar.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = lugar.categoria,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            
                            // Badge de estado
                            androidx.compose.material3.AssistChip(
                                onClick = { },
                                label = { Text("Pendiente") },
                                colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFF9800).copy(alpha = 0.1f),
                                    labelColor = Color(0xFFFF9800)
                                )
                            )
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        
                        // Descripción
                        Text(
                            text = lugar.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 3,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Información adicional
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material.icons.Icons.Default.Phone
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = lugar.telefono,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material.icons.Icons.Default.LocationOn
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = lugar.direccion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        // Botones de acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.AUTORIZADO)
                                    moderacionViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.AUTORIZADO)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ) {
                                Text("✅ Autorizar", color = Color.White)
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.RECHAZADO)
                                    moderacionViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.RECHAZADO)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ) {
                                Text("❌ Rechazar")
                            }
                        }
                    }
                }
            }
        }
        }
    }
}


