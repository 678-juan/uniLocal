package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.unilocal.R
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.unilocal.ui.theme.AzulEnlaces
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.remember
import com.example.unilocal.viewModel.UsuarioViewModel
import com.example.unilocal.ui.pantallas.usuario.navegacionUsuario.RutaTab

@Composable
fun Guardados(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel? = null,
    lugaresViewModel: com.example.unilocal.viewModel.LugaresViewModel? = null
) {
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val lugaresVM: com.example.unilocal.viewModel.LugaresViewModel = lugaresViewModel ?: viewModel()
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val lugares by lugaresVM.lugares.collectAsState()
    val favoritosIds by viewModel.favoritosGuardados.collectAsState()

    // avatares disponibles
    val avatares = listOf(
        R.drawable.hombre, R.drawable.mujer, R.drawable.hombre1,
        R.drawable.mujer1, R.drawable.hombre2, R.drawable.mujer2
    )

    // datos del usuario o por defecto
    val usuarioUsername = usuarioActual?.username ?: "@vawlte"
    val avatarId = (usuarioActual?.avatar ?: 0).coerceIn(0, avatares.size - 1)

    // obtener lugares guardados: mapear los IDs de favoritos a objetos Lugar
    // Si faltan lugares cargados localmente, pedirlos al ViewModel de lugares
    LaunchedEffect(favoritosIds) {
        // buscar en Firebase cualquier lugar que no esté en la lista local
        favoritosIds.forEach { id ->
            val existe = lugares.any { it.id == id }
            if (!existe) {
                lugaresVM.buscarPorId(id)
            }
        }
    }

    val lugaresGuardados = lugares.filter { favoritosIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // header con flecha de regreso y título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tus guardados",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // línea decorativa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        // tarjeta de cuenta de usuario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tu cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = avatares.getOrElse(avatarId) { avatares[0] }),
                        contentDescription = "Avatar del Usuario",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = usuarioUsername,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }

        // grid de lugares guardados
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            if (lugaresGuardados.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lugaresGuardados) { lugar ->
                        LugarGuardadoItem(
                            lugar = lugar,
                            onClick = {
                                // navegar a detalles del lugar
                                navController.navigate(RutaTab.LugarDetalles(lugar.id))
                            }
                        )
                    }
                }
            } else {
                // mostrar mensaje cuando no hay lugares guardados
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tienes lugares guardados",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "Explora lugares y guárdalos como favoritos",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))



    }
}

@Composable
fun LugarGuardadoItem(
    lugar: com.example.unilocal.model.entidad.Lugar,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
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
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = lugar.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                )
            }

            Text(
                text = lugar.nombre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(8.dp),
                maxLines = 2
            )
        }
    }
}

