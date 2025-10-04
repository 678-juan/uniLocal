package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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

            IconButton(
                onClick = { navegarAConfiguracion() },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Configuración")
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color.LightGray
        )

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
                    FichaLugar(
                        lugar = lugar,
                        onClick = { navegarALugar(lugar.id) }
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
}