package com.example.unilocal.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.R
import com.example.unilocal.model.entidad.Usuario
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.ui.componentes.LineaDecorativa
import com.example.unilocal.ui.componentes.SelectorAvatar
import com.example.unilocal.viewModel.UsuarioViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegisto(
    navegarALogin: () -> Unit = {},
    usuarioViewModel: UsuarioViewModel? = null
) {
    var nombre by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Masculino") }
    var password by remember { mutableStateOf("") }
    var avatarSeleccionado by remember { mutableStateOf(0) }
    var cargando by remember { mutableStateOf(false) }

    val contexto = LocalContext.current
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val scrollState = rememberLazyListState()
    var sexoExpandido by remember { mutableStateOf(false) }

    // Debug: mostrar usuarios después del registro
    LaunchedEffect(Unit) {
        println("Usuarios después del registro: ${viewModel.usuario.value.map { it.email }}")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        state = scrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Logo y líneas decorativas
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Líneas decorativas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LineaDecorativa(200.dp)
                    LineaDecorativa(250.dp)
                }

                // Logo centrado
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .size(270.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 60.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Text(
                text = stringResource(R.string.register_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            CampoTexto(
                valor = nombre,
                cuandoCambia = { nombre = it },
                etiqueta = stringResource(R.string.nombre_hint),
                modificador = Modifier.fillMaxWidth()
            )
        }

        item {
            CampoTexto(
                valor = username,
                cuandoCambia = { username = it },
                etiqueta = stringResource(R.string.username_hint),
                modificador = Modifier.fillMaxWidth()
            )
        }

        item {
            CampoTexto(
                valor = email,
                cuandoCambia = { email = it },
                etiqueta = stringResource(R.string.email_hint),
                modificador = Modifier.fillMaxWidth(),
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }

        item {
            CampoTexto(
                valor = ciudad,
                cuandoCambia = { ciudad = it },
                etiqueta = stringResource(R.string.ciudad_hint),
                modificador = Modifier.fillMaxWidth()
            )
        }

        item {
            // Dropdown para sexo
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sexo",
                    fontSize = 14.sp,
                    color = androidx.compose.ui.graphics.Color.DarkGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = sexoExpandido,
                    onExpandedChange = { sexoExpandido = !sexoExpandido }
                ) {
                    OutlinedTextField(
                        value = sexo,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = sexoExpandido
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .background(androidx.compose.ui.graphics.Color.White)
                    )

                    if (sexoExpandido) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(androidx.compose.ui.graphics.Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
                        ) {
                            Column {
                                DropdownMenuItem(
                                    text = { Text("Masculino") },
                                    onClick = {
                                        sexo = "Masculino"
                                        sexoExpandido = false
                                    },
                                    modifier = Modifier.background(androidx.compose.ui.graphics.Color.White)
                                )
                                DropdownMenuItem(
                                    text = { Text("Femenino") },
                                    onClick = {
                                        sexo = "Femenino"
                                        sexoExpandido = false
                                    },
                                    modifier = Modifier.background(androidx.compose.ui.graphics.Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            CampoTexto(
                valor = password,
                cuandoCambia = { password = it },
                etiqueta = stringResource(R.string.password_hint),
                modificador = Modifier.fillMaxWidth(),
                transformacion = PasswordVisualTransformation(),
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        item {
            // Selector de Avatar
            SelectorAvatar(
                avatarSeleccionado = avatarSeleccionado,
                onAvatarSeleccionado = { avatarSeleccionado = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            BotonPrincipal(
                texto = if (cargando) "Registrando..." else stringResource(R.string.register_button),
                onClick = {
                    if (nombre.isBlank() || username.isBlank() || email.isBlank() ||
                        ciudad.isBlank() || sexo.isBlank() || password.isBlank()
                    ) {
                        Toast.makeText(
                            contexto,
                            "Por favor completa todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@BotonPrincipal
                    }

                    if (sexo.isBlank()) {
                        Toast.makeText(contexto, "Por favor selecciona tu sexo", Toast.LENGTH_SHORT)
                            .show()
                        return@BotonPrincipal
                    }

                    cargando = true

                    // Verificar si el email ya existe
                    val usuarioExistente = viewModel.buscarEmail(email.trim())
                    if (usuarioExistente != null) {
                        Toast.makeText(
                            contexto,
                            "Este email ya está registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        cargando = false
                        return@BotonPrincipal
                    }

                    // Crear nuevo usuario
                    val nuevoUsuario = Usuario(
                        id = (viewModel.usuario.value.size + 1).toString(),
                        nombre = nombre.trim(),
                        username = username.trim(),
                        clave = password,
                        email = email.trim(),
                        ciudad = ciudad.trim(),
                        sexo = sexo.trim(),
                        avatar = avatarSeleccionado
                    )

                    viewModel.crearUsuario(nuevoUsuario)

                    Toast.makeText(
                        contexto,
                        "¡Usuario registrado exitosamente!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navegarALogin()

                    cargando = false
                }
            )
        }

    }
}