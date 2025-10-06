package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.R
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.viewModel.UsuarioViewModel
import kotlin.math.roundToInt

// Función para calcular el promedio de estrellas
fun calcularPromedioEstrellas(lugar: Lugar): Double {
    return if (lugar.comentarios.isNotEmpty()) {
        lugar.comentarios.map { it.estrellas }.average()
    } else {
        0.0
    }
}

// Componente para mostrar las estrellas
@Composable
fun MostrarEstrellas(
    promedio: Double,
    modifier: Modifier = Modifier
) {
    val promedioRedondeado = promedio.roundToInt()
    val tieneDecimales = promedio % 1 != 0.0
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            val estrellaActual = index + 1
            Icon(
                imageVector = if (estrellaActual <= promedioRedondeado) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.Star
                },
                contentDescription = "Estrella $estrellaActual",
                tint = if (estrellaActual <= promedioRedondeado) {
                    Color(0xFFFFD700) // Color dorado para estrellas llenas
                } else {
                    Color.Gray
                },
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", promedio),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

@Composable
fun PublicacionUno(
    lugar: Lugar,
    onClick: () -> Unit,
    usuarioViewModel: UsuarioViewModel? = null
) {
    // buscar quien creo el lugar
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val creador = viewModel.buscarId(lugar.creadorId)
    
    // like state persistente
    val yaDioLike by viewModel.likesDados.collectAsState()
    val dioLike = yaDioLike.contains(lugar.id)
    var likes by remember { mutableStateOf(lugar.likes.toInt()) }
    
    // bookmark state persistente
    val favoritosGuardados by viewModel.favoritosGuardados.collectAsState()
    var estaGuardado = favoritosGuardados.contains(lugar.id)
    
    // avatares disponibles
    val avatares = listOf(
        R.drawable.hombre, R.drawable.mujer, R.drawable.hombre1,
        R.drawable.mujer1, R.drawable.hombre2, R.drawable.mujer2
    )

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

            // header con avatar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // avatar del creador
                Image(
                    painter = painterResource(id = avatares[creador?.avatar ?: 0]),
                    contentDescription = "Avatar del Creador",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = creador?.nombre ?: "Usuario desconocido",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = lugar.estado.name,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    MostrarEstrellas(
                        promedio = calcularPromedioEstrellas(lugar)
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

                val corazonIcon = if (dioLike) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

                Icon(
                    imageVector = corazonIcon,
                    contentDescription = "Like",
                    tint = if (dioLike) Color.Red else Color.Gray,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            if (dioLike) {
                                // Quitar like
                                viewModel.quitarLike(lugar.id)
                                likes = maxOf(0, likes - 1)
                            } else {
                                // Dar like
                                viewModel.darLike(lugar.id)
                                likes += 1
                            }
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

                // comentarios
                Text(
                    text = "💬 ${lugar.comentarios.size}", // no hay iconos de comentarios
                    modifier = Modifier
                        .clickable { /* abrir comentarios */ }
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // icono de guardar (bookmark)
                val bookmarkIcon = if (estaGuardado) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                
                Icon(
                    imageVector = bookmarkIcon,
                    contentDescription = "Guardar en favoritos",
                    tint = if (estaGuardado) Color(0xFF2196F3) else Color.Gray,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            estaGuardado = !estaGuardado
                            if (estaGuardado) {
                                // Agregar a favoritos
                                viewModel.agregarFavorito(lugar)
                            } else {
                                // Quitar de favoritos
                                viewModel.quitarFavorito(lugar)
                            }
                        }
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
