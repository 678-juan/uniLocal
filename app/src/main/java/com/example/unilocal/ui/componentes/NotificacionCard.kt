package com.example.unilocal.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.model.entidad.Notificacion
import com.example.unilocal.model.entidad.TipoNotificacion
import com.example.unilocal.viewModel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificacionCard(
    notificacion: Notificacion,
    usuarioViewModel: UsuarioViewModel? = null,
    onNotificacionClick: (Notificacion) -> Unit = {}
) {
    val viewModel = usuarioViewModel ?: androidx.lifecycle.viewmodel.compose.viewModel<UsuarioViewModel>()
    
    // Obtener icono según el tipo de notificación
    val icono = when (notificacion.tipo) {
        TipoNotificacion.LUGAR_AUTORIZADO -> Icons.Default.CheckCircle
        TipoNotificacion.LUGAR_RECHAZADO -> Icons.Default.Notifications
        TipoNotificacion.COMENTARIO_NUEVO -> Icons.Default.Notifications
        TipoNotificacion.LUGAR_FAVORITO -> Icons.Default.LocationOn
    }
    
    // Obtener color según el tipo
    val colorIcono = when (notificacion.tipo) {
        TipoNotificacion.LUGAR_AUTORIZADO -> Color(0xFF4CAF50)
        TipoNotificacion.LUGAR_RECHAZADO -> Color(0xFFF44336)
        TipoNotificacion.COMENTARIO_NUEVO -> Color(0xFF2196F3)
        TipoNotificacion.LUGAR_FAVORITO -> Color(0xFFFF9800)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { 
                onNotificacionClick(notificacion)
                if (!notificacion.leida) {
                    viewModel.marcarNotificacionComoLeida(notificacion.id)
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) 
                Color.White 
            else 
                Color(0xFFE3F2FD).copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notificacion.leida) 2.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de la notificación
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colorIcono.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = "Tipo de notificación",
                    tint = colorIcono,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido de la notificación
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título y estado de leída
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificacion.titulo,
                        fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (notificacion.leida) Color.Gray else Color.Black
                    )
                    
                    // Indicador de no leída
                    if (!notificacion.leida) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colorIcono)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Mensaje
                Text(
                    text = notificacion.mensaje,
                    fontSize = 14.sp,
                    color = if (notificacion.leida) Color.Gray else Color.DarkGray,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Fecha y hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatearFechaCompleta(notificacion.fecha),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    // Tipo de notificación
                    Text(
                        text = obtenerTextoDelTipo(notificacion.tipo),
                        fontSize = 10.sp,
                        color = colorIcono,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Función para formatear fecha completa
private fun formatearFechaCompleta(timestamp: Long): String {
    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp
    
    return when {
        diferencia < 60000 -> "Hace un momento"
        diferencia < 3600000 -> "Hace ${diferencia / 60000} min"
        diferencia < 86400000 -> "Hace ${diferencia / 3600000} h"
        diferencia < 604800000 -> "Hace ${diferencia / 86400000} días"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

// Función para obtener texto del tipo
private fun obtenerTextoDelTipo(tipo: TipoNotificacion): String {
    return when (tipo) {
        TipoNotificacion.LUGAR_AUTORIZADO -> "Autorización"
        TipoNotificacion.LUGAR_RECHAZADO -> "Rechazo"
        TipoNotificacion.COMENTARIO_NUEVO -> "Comentario"
        TipoNotificacion.LUGAR_FAVORITO -> "Favorito"
    }
}
