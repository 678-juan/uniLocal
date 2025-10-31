package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.R
import com.example.unilocal.viewModel.UsuarioViewModel

@Composable
fun ComentarioCard(
    comentario: com.example.unilocal.model.entidad.Comentario,
    usuarioViewModel: UsuarioViewModel,
    lugar: com.example.unilocal.model.entidad.Lugar? = null,
    esSeleccionado: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usuario = usuarioViewModel.obtenerUsuarioPorId(comentario.usuarioId)
    val dueñoLugar = lugar?.let { usuarioViewModel.obtenerUsuarioPorId(it.creadorId) }
    
    // Cargar usuario desde Firebase si no está en local
    LaunchedEffect(comentario.usuarioId) {
        if (usuario == null) {
            usuarioViewModel.buscarId(comentario.usuarioId)
        }
    }
    
    // Cargar dueño del lugar desde Firebase si no está en local
    LaunchedEffect(lugar?.creadorId) {
        if (dueñoLugar == null && lugar != null) {
            usuarioViewModel.buscarId(lugar.creadorId)
        }
    }
    
    // Usar usuario actualizado después de la búsqueda
    val usuarioActualizado by usuarioViewModel.usuarioActual.collectAsState()
    val usuarioFinal = usuario ?: (if (usuarioActualizado?.id == comentario.usuarioId) usuarioActualizado else null)
    val dueñoLugarFinal = dueñoLugar ?: (if (usuarioActualizado?.id == lugar?.creadorId) usuarioActualizado else null)

    if (usuarioFinal != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    if (esSeleccionado) Color(0xFFF0F0F0) else Color.White, 
                    shape = RoundedCornerShape(12.dp)
                )
                .border(2.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🧍 Avatar del usuario - memoizado
            val avatarRes = remember(usuarioFinal.avatar) {
                when (usuarioFinal.avatar) {
                    0 -> R.drawable.hombre
                    1 -> R.drawable.mujer
                    2 -> R.drawable.hombre1
                    3 -> R.drawable.mujer1
                    4 -> R.drawable.hombre2
                    5 -> R.drawable.mujer2
                    else -> R.drawable.hombre // por defecto
                }
            }

            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Avatar de ${usuarioFinal.nombre}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 💬 Contenido del comentario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuarioFinal.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comentario.texto,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // ⭐ Estrellas de calificación
                Row {
                    repeat(comentario.estrellas) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        
        // Mostrar respuesta si existe
        if (comentario.respuesta != null && dueñoLugarFinal != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Avatar del dueño del lugar
                    val avatarRes = when (dueñoLugarFinal.avatar) {
                        0 -> R.drawable.hombre
                        1 -> R.drawable.mujer
                        2 -> R.drawable.hombre1
                        3 -> R.drawable.mujer1
                        4 -> R.drawable.hombre2
                        5 -> R.drawable.mujer2
                        else -> R.drawable.logo // por defecto
                    }
                    
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = "Dueño del lugar",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFF4CAF50), CircleShape)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Nombre del dueño
                        Text(
                            text = "Dueño del lugar • ${dueñoLugarFinal.nombre}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        // Indicador de respuesta
                        Text(
                            text = "Respondió",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Black,
                                fontSize = 10.sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Texto de la respuesta
                        Text(
                            text = comentario.respuesta,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }
    } else {
        Text(
            text = "Comentario no disponible",
            color = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )
    }
    
}