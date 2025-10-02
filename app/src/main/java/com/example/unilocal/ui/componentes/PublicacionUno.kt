package com.example.unilocal.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.unilocal.model.entidad.Lugar

@Composable
fun PublicacionUno(
    lugar: Lugar,
    onClick: () -> Unit,
) {
    // Estado local para controlar si el usuario ya dio like
    var yaDioLike by remember { mutableStateOf(false) }
    var likes by remember { mutableStateOf(lugar.likes.toInt()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {

            // --- CABECERA DE LA PUBLICACIÓN (CREADOR) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Creador: ${lugar.creadorId}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lugar.estado.name,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Person, contentDescription = "Opciones")
            }


            FichaLugar(
                lugar = lugar,
                onClick = { onClick() },
                modifier = Modifier.height(180.dp)
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val corazonIcon = if (yaDioLike) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

                Icon(
                    imageVector = corazonIcon,
                    contentDescription = "Like",
                    tint = if (yaDioLike) Color.Red else Color.Gray,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(enabled = !yaDioLike) {
                            likes += 1
                            yaDioLike = true

                        }
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$likes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Comentarios
                Text(
                    text = "💬 ${lugar.comentarios.size}", // no encuentro iconos de ommentarios
                    modifier = Modifier
                        .clickable { /* abrir comentarios */ }
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
