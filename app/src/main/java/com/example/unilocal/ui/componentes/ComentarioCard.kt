package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unilocal.R
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel

@Composable
fun ComentarioCard(
    comentario: com.example.unilocal.model.entidad.Comentario,
    usuarioViewModel: UsuarioViewModel,
    modifier: Modifier = Modifier
) {
    val usuario = usuarioViewModel.buscarId(comentario.usuarioId)

    if (usuario != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(2.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🧍 Avatar del usuario
            val avatarRes = when (usuario.avatar) {
                0 -> R.drawable.hombre
                1 -> R.drawable.mujer
                2 -> R.drawable.hombre1
                3 -> R.drawable.mujer1
                4 -> R.drawable.hombre2
                5 -> R.drawable.mujer2
                else -> R.drawable.hombre // por defecto
            }

            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Avatar de ${usuario.nombre}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 💬 Contenido del comentario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombre,
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
    } else {
        Text(
            text = "Comentario no disponible",
            color = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )
    }
}