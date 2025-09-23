package com.example.unilocal.ui.pantallas.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.CampoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarUsuario(
    onNavigateBack: () -> Unit = {}
) {

    var nombre by remember { mutableStateOf("Nombre Ejemplo") }
    var username by remember { mutableStateOf("username_ejemplo") }
    var email by remember { mutableStateOf("ejemplo@correo.com") }
    var ciudad by remember { mutableStateOf("Ciudad Ejemplo") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarNuevaContrasena by remember { mutableStateOf("") }

    // Estados para errores
    var mostrarErrorContrasena by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.editar_perfil_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Nombre
            CampoTexto(
                valor = nombre,
                cuandoCambia = { nombre = it },
                etiqueta = stringResource(id = R.string.nombre_usuario)
            )

            // Username
            CampoTexto(
                valor = username,
                cuandoCambia = { username = it },
                etiqueta = stringResource(id = R.string.username_hint)
            )

            // Email
            CampoTexto(
                valor = email,
                cuandoCambia = { email = it },
                etiqueta = stringResource(id = R.string.email_usuario)
            )

            // Ciudad
            CampoTexto(
                valor = ciudad,
                cuandoCambia = { ciudad = it },
                etiqueta = stringResource(id = R.string.ciudad_usuario)
            )

            Spacer(modifier = Modifier.weight(1f))

            BotonPrincipal(
                texto = stringResource(id = R.string.boton_editar_datos_usuario),
                onClick = {
                    if (nuevaContrasena.isNotEmpty() && nuevaContrasena != confirmarNuevaContrasena) {
                        mostrarErrorContrasena = true
                    } else {

                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PantallaEditarUsuarioPreview() {
    // Tema {
    PantallaEditarUsuario()
    // }
}
