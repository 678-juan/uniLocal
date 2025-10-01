package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import com.example.unilocal.R
import com.example.unilocal.ui.configuracion.RutasPantallas


@Composable
fun Perfil(navController: NavController) {

    val usuarioNombre = "Jose Maria dos Santos"
    val usuarioUsername = "@sm1llle"
    val descripcion = "Me gusta"
    val ciudad = "Armenia"

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
                painter = painterResource(id = R.drawable.perfilusuario),
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

        // Cuerpo del Perfil (Informacion y Botón de Configuracion)
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

                // Descripción o bio
                Text(text = descripcion, fontSize = 14.sp)

                // Datos requeridos
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Ciudad: $ciudad", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }

            // Botón de Configuración/Editar Perfil
            IconButton(
                onClick = {
                    // Navega usando la ruta global
                    navController.navigate(RutasPantallas.EditarPerfilUsuario)
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Editar Perfil")
            }
        }

        // Uso de HorizontalDivider
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

        //  Sección de Publicaciones/Lugares Creados
        Text(
            text = "Publicación",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        // La barra azul del diseño
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(3.dp)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.Start)
                .offset(x = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CTA para crear un nuevo lugar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Navega usando la ruta global (ahora resuelta)
                    navController.navigate(RutasPantallas.CrearLugar)
                }
                .padding(vertical = 24.dp)
        ) {
            Text(text = "¡Añade un lugar nuevo!", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

            // Icono nueva publicacion
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
                text = "Nueva publicación",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Sección de Lista de Lugares Creados
        Text(
            text = "Lugares creados por mí (Pendiente: Lista y Respuesta a Comentarios)",
            color = Color.DarkGray,
            modifier = Modifier.padding(16.dp)
        )
    }
}