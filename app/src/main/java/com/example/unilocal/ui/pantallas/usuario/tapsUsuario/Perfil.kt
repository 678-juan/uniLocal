package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.unilocal.R
import com.example.unilocal.ui.configuracion.RutasPantallas
import com.example.unilocal.ui.componentes.FichaLugar
import com.example.unilocal.ui.componentes.FichaLugarPerfil
import com.example.unilocal.ui.componentes.ComentarioCard
import com.example.unilocal.ui.componentes.NotificacionCard
import com.example.unilocal.viewModel.UsuarioViewModel
import com.example.unilocal.viewModel.LugaresViewModel


@Composable
fun Perfil(
    navegarACrearLugar: () -> Unit,
    usuarioViewModel: UsuarioViewModel? = null,
    lugaresViewModel: LugaresViewModel? = null,
    navegarALugar: (String) -> Unit = {},
    navegarAConfiguracion: () -> Unit = {}
) {
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val lugaresVM: LugaresViewModel = lugaresViewModel ?: viewModel()
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val lugares by lugaresVM.lugares.collectAsState()
    
    var lugarSeleccionado by remember { mutableStateOf<com.example.unilocal.model.entidad.Lugar?>(null) }
    var mostrarComentarios by remember { mutableStateOf(false) }
    var comentarioSeleccionado by remember { mutableStateOf<com.example.unilocal.model.entidad.Comentario?>(null) }
    var mostrarNotificaciones by remember { mutableStateOf(false) }
    
    // Obtener notificaciones del usuario
    val notificaciones by viewModel.notificacionesUsuario.collectAsState()
    val notificacionesNoLeidas = viewModel.obtenerNotificacionesNoLeidas()
    var textoRespuesta by remember { mutableStateOf("") }

    // avatares disponibles
    val avatares = listOf(
        R.drawable.hombre, R.drawable.mujer, R.drawable.hombre1,
        R.drawable.mujer1, R.drawable.hombre2, R.drawable.mujer2
    )

    // datos del usuario o por defecto
    val usuarioNombre = usuarioActual?.nombre ?: "Usuario"
    val usuarioUsername = usuarioActual?.username ?: "@usuario"
    val descripcion = "Me gusta"
    val ciudad = usuarioActual?.ciudad ?: "Ciudad"
    val avatarId = (usuarioActual?.avatar ?: 0).coerceIn(0, avatares.size - 1)

    // lugares que creo este usuario
    val lugaresDelUsuario = lugares.filter { lugar ->
        lugar.creadorId == usuarioActual?.id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fotoperfil),
                contentDescription = "Imagen de Portada",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Image(
                painter = painterResource(id = avatares.getOrElse(avatarId) { avatares[0] }),
                contentDescription = "Avatar del Usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = 30.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Cuerpo del Perfil (Informacion y Botón de Configuración)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuarioUsername,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(text = usuarioNombre, fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = descripcion, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ciudad: $ciudad",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterVertically),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón de notificaciones
                Box {
                    IconButton(
                        onClick = { mostrarNotificaciones = !mostrarNotificaciones }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                    // Badge para notificaciones no leídas
                    if (notificacionesNoLeidas > 0) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = notificacionesNoLeidas.toString(),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                
                // Botón de configuración
                IconButton(
                    onClick = { navegarAConfiguracion() }
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración")
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color.LightGray
        )

        // Sección de notificaciones
        if (mostrarNotificaciones) {
            Text(
                text = "Notificaciones",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (notificaciones.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Sin notificaciones",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes notificaciones",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Te notificaremos cuando tengas novedades",
                        color = Color.Gray.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notificaciones) { notificacion ->
                        NotificacionCard(
                            notificacion = notificacion,
                            usuarioViewModel = viewModel,
                            onNotificacionClick = { notif ->
                                // Aquí puedes agregar lógica adicional cuando se hace click
                                // Por ejemplo, navegar al lugar si tiene lugarId
                            }
                        )
                    }
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.LightGray
            )
        }

        // Sección de publicaciones
        Text(
            text = stringResource(R.string.Publicación),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(3.dp)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.Start)
                .offset(x = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (lugaresDelUsuario.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                lugaresDelUsuario.forEach { lugar ->
                    FichaLugarPerfil(
                        lugar = lugar,
                        onClick = { navegarALugar(lugar.id) },
                        onBorrar = { lugaresVM.borrarLugar(lugar.id) },
                        onVerComentarios = {
                            lugarSeleccionado = lugar
                            mostrarComentarios = true
                        }
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navegarACrearLugar() }
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.Añade_un_lugar_nuevo),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir Lugar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(60.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clip(CircleShape)
                        .padding(8.dp)
                )

                Text(
                    text = stringResource(R.string.Nueva_publicacion),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Text(
                text = stringResource(R.string.Descripcion_lugar_para_el_usuario),
                color = Color.DarkGray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    
    // Modal para comentarios con respuesta
    if (mostrarComentarios && lugarSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { 
                mostrarComentarios = false
                lugarSeleccionado = null
                comentarioSeleccionado = null
                textoRespuesta = ""
            },
            containerColor = Color.White,
            text = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        val lugar = lugarSeleccionado
                        if (lugar != null) {
                            if (lugar.comentarios.isEmpty()) {
                                Text(
                                    text = "No hay comentarios aún",
                                    color = Color.Black,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(300.dp)
                                ) {
                                items(lugar.comentarios) { comentario ->
                                    val esSeleccionado = comentarioSeleccionado?.id == comentario.id
                                    ComentarioCard(
                                        comentario = comentario,
                                        usuarioViewModel = viewModel,
                                        lugar = lugar,
                                        esSeleccionado = esSeleccionado,
                                        onClick = {
                                            comentarioSeleccionado = comentario
                                            textoRespuesta = comentario.respuesta ?: ""
                                        }
                                    )
                                }
                                }
                            }
                            
                            // Campo para responder
                            if (comentarioSeleccionado != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = textoRespuesta,
                                    onValueChange = { textoRespuesta = it },
                                    label = { Text("Tu respuesta...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )
                            }
                        } else {
                            Text(
                                text = "Error: Lugar no encontrado",
                                color = Color.Black
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    if (comentarioSeleccionado != null) {
                        Button(
                            onClick = {
                                if (textoRespuesta.isNotBlank()) {
                                    lugaresVM.responderComentario(
                                        lugarSeleccionado!!.id,
                                        comentarioSeleccionado!!.id,
                                        textoRespuesta
                                    )
                                    comentarioSeleccionado = null
                                    textoRespuesta = ""
                                }
                            },
                            enabled = textoRespuesta.isNotBlank()
                        ) {
                            Text("Responder")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(
                        onClick = { 
                            mostrarComentarios = false
                            lugarSeleccionado = null
                            comentarioSeleccionado = null
                            textoRespuesta = ""
                        }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        )
    }
}

// Función para formatear el tiempo transcurrido
private fun formatearTiempo(timestamp: Long): String {
    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp
    
    return when {
        diferencia < 60000 -> "unos segundos"
        diferencia < 3600000 -> "${diferencia / 60000} minutos"
        diferencia < 86400000 -> "${diferencia / 3600000} horas"
        diferencia < 604800000 -> "${diferencia / 86400000} días"
        else -> "${diferencia / 604800000} semanas"
    }
}
