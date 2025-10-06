package com.example.unilocal.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.ui.theme.AzulEnlaces
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.LineaDecorativa
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.R
import com.example.unilocal.viewModel.UsuarioViewModel
import com.example.unilocal.viewModel.ModeradorViewModel
import com.example.unilocal.model.entidad.Usuario
import com.example.unilocal.model.entidad.Moderador

@Composable
fun PantallaLogin(
    navegarARegistro: () -> Unit,
    navegarAPrincipalUsuario: () -> Unit,
    navegarAPrincipalAdmin: (String) -> Unit,
    usuarioViewModel: UsuarioViewModel? = null,
    moderadorViewModel: ModeradorViewModel? = null
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    
    // Debug: mostrar usuarios disponibles
    LaunchedEffect(Unit) {
        println("Usuarios disponibles para login: ${viewModel.usuario.value.map { it.email }}")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // lineas :c
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LineaDecorativa(200.dp)
            LineaDecorativa(250.dp)
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .size(270.dp)
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(260.dp))


            Text(stringResource(R.string.login_title))

            Spacer(modifier = Modifier.height(32.dp))

            // Campo email
            CampoTexto(
                valor = correo,
                cuandoCambia = { correo = it },
                etiqueta = "Correo electrónico",
                modificador = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // C contraseña
            CampoTexto(
                valor = clave,
                cuandoCambia = { clave = it },
                etiqueta = stringResource(R.string.clave_hint),
                modificador = Modifier.fillMaxWidth(),
                transformacion = PasswordVisualTransformation(),
                opcionesTeclado = KeyboardOptions.Default.copy()
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Rcontraseña
            Text(
                text = stringResource(R.string.forgot_password),
                fontSize = 14.sp,
                color = AzulEnlaces,
                modifier = Modifier.clickable { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // boton entrar
            BotonPrincipal(
                texto = if (cargando) "Iniciando sesión..." else stringResource(R.string.login_button),
                onClick = {
                    if (correo.isBlank() || clave.isBlank()) {
                        Toast.makeText(contexto, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@BotonPrincipal
                    }
                    
                    cargando = true
                    
                    // Intentar login como moderador primero
                    val moderador = moderadorViewModel?.login(correo.trim(), clave)
                    if (moderador != null) {
                        Toast.makeText(contexto, "¡Bienvenido ${moderador.nombre} (moderador)!", Toast.LENGTH_SHORT).show()
                        navegarAPrincipalAdmin(moderador.id)
                    } else {
                        // Si no es moderador, intentar como usuario
                        val usuarioEncontrado = viewModel.login(correo.trim(), clave)
                        if (usuarioEncontrado != null) {
                            Toast.makeText(contexto, "¡Bienvenido ${usuarioEncontrado.nombre}!", Toast.LENGTH_SHORT).show()
                            navegarAPrincipalUsuario()
                        } else {
                            Toast.makeText(contexto, "Credenciales incorrectas. Verifica tu email y contraseña.", Toast.LENGTH_LONG).show()
                        }
                    }
                    
                    cargando = false
                }
            )


            Spacer(modifier = Modifier.height(24.dp))

            // registrarse
            Text(
                text = stringResource(R.string.register_prompt),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = AzulEnlaces,
                modifier = Modifier.clickable {
                    navegarARegistro()
                }
            )
        }
    }


}
