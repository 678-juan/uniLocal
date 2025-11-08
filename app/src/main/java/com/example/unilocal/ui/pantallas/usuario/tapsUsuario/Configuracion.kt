package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.unilocal.ui.theme.AzulEnlaces
import com.example.unilocal.viewModel.UsuarioViewModel

@Composable
fun Configuracion(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel? = null,
    navegarALogin: () -> Unit = {}
) {
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val usuarioActual by viewModel.usuarioActual.collectAsState()

    // avatares disponibles
    val avatares = listOf(
        R.drawable.hombre, R.drawable.mujer, R.drawable.hombre1,
        R.drawable.mujer1, R.drawable.hombre2, R.drawable.mujer2
    )

    // datos del usuario o por defecto
    val usuarioNombre = usuarioActual?.nombre ?: "Karol"
    val usuarioUsername = usuarioActual?.username ?: "@vawlte"
    val avatarId = (usuarioActual?.avatar ?: 0).coerceIn(0, avatares.size - 1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header con flecha de regreso y título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                //cambie aqui
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Configuración",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Línea decorativa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )


        // Tarjeta de cuenta de usuario
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

        // Opciones del menú
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                // Guardados
                MenuOption(
                    text = "Guardados",
                    onClick = { navController.navigate(com.example.unilocal.ui.pantallas.usuario.navegacionUsuario.RutaTab.Guardados) }
                )

                Divider(color = Color.LightGray, thickness = 0.5.dp)


                // Editar datos
                MenuOption(
                    text = "Editar datos",
                    onClick = { navController.navigate(com.example.unilocal.ui.pantallas.usuario.navegacionUsuario.RutaTab.EditarUsuario) }
                )

                Divider(color = Color.LightGray, thickness = 0.5.dp)

                // Cerrar sesión
                MenuOption(
                    text = "Cerrar sesión",
                    onClick = {
                        viewModel.cerrarSesion()
                        navegarALogin()
                    },
                    textColor = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //texto abajo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Necesitas ayuda. ",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "ayuda.",
                fontSize = 14.sp,
                color = AzulEnlaces,
                modifier = Modifier.clickable { /* TODO: Implementar ayuda */ }
            )
        }
    }
}

@Composable
fun MenuOption(
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor
        )
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "Ir a $text",
            tint = Color.Gray
        )
    }
}

