package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun FichaLugarAdmin(
    lugar: Lugar,
    estado: String = "",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.Black, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // imagen
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            ) {
                if (lugar.imagenUri.startsWith("content://") || lugar.imagenUri.startsWith("file://")) {
                    // uri
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
                    // drawable
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
            
            Spacer(Modifier.width(12.dp))
            
            // información
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lugar.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = lugar.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = lugar.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // estado
            if (estado.isNotEmpty()) {
                AssistChip(
                    onClick = {},
                    label = { 
                        Text(
                            estado,
                            color = when (estado.uppercase()) {
                                "AUTORIZADO" -> Color.Green
                                "RECHAZADO" -> Color.Red
                                "PENDIENTE" -> Color.Blue
                                else -> Color.Gray
                            },
                            fontSize = 10.sp
                        ) 
                    }
                )
            }
        }
    }
}
