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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.viewModel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarUsuario(
    navController: androidx.navigation.NavController? = null,
    usuarioViewModel: UsuarioViewModel? = null
) {
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val usuarioActual by viewModel.usuarioActual.collectAsState()

    var nombre by remember { mutableStateOf(usuarioActual?.nombre ?: "") }
    var username by remember { mutableStateOf(usuarioActual?.username ?: "") }
    var email by remember { mutableStateOf(usuarioActual?.email ?: "") }
    var ciudad by remember { mutableStateOf(usuarioActual?.ciudad ?: "") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarNuevaContrasena by remember { mutableStateOf("") }

    var mostrarErrorContrasena by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.editar_datos_titulo)) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
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

            // Email (solo lectura)
            OutlinedTextField(
                value = email,
                onValueChange = { }, // no permite cambios
                label = { Text(stringResource(id = R.string.email_usuario)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // campo deshabilitado
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Gray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray
                )
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
                        // Actualizar datos del usuario
                        viewModel.actualizarUsuario(nombre, username, email, ciudad, if (nuevaContrasena.isNotEmpty()) nuevaContrasena else null)
                        navController?.popBackStack()
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

