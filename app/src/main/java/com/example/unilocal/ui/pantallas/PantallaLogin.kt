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
import com.example.unilocal.ui.componentes.Resultadooperacion

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
    var intentandoLoginModerador by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val viewModel: UsuarioViewModel = usuarioViewModel ?: viewModel()
    val moderadorVM: ModeradorViewModel = moderadorViewModel ?: viewModel()

    val usuarioResult by viewModel.usuarioResult.collectAsState()
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val moderadorActual by moderadorVM.moderadorActual.collectAsState()
    


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
                    intentandoLoginModerador = true

                    // Intentar login como moderador primero (puede retornar null si busca en Firebase)
                    val moderador = moderadorVM.login(correo.trim(), clave)
                    if (moderador != null) {
                        // Moderador encontrado en local, navegar inmediatamente
                        Toast.makeText(contexto, "¡Bienvenido ${moderador.nombre} (moderador)!", Toast.LENGTH_SHORT).show()
                        cargando = false
                        intentandoLoginModerador = false
                        navegarAPrincipalAdmin(moderador.id)
                        // Limpiar campos
                        correo = ""
                        clave = ""
                        return@BotonPrincipal
                    }

                    // Si retornó null, está buscando en Firebase (asíncrono)
                    // El LaunchedEffect se encargará de esperar y luego intentar como usuario si no hay resultado
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Manejar resultado del login
            Resultadooperacion(
                result = usuarioResult,
                onSucess = {
                    // El login fue exitoso, usuarioActual ya está actualizado
                    viewModel.resetear()
                },
                onFailure = {
                    cargando = false
                    viewModel.resetear()
                }
            )
            
            // Observar cuando el moderador se loguea exitosamente (después de buscar en Firebase)
            LaunchedEffect(moderadorActual) {
                if (moderadorActual != null && intentandoLoginModerador && correo.isNotBlank()) {
                    // Verificar que el email coincide (para evitar navegación incorrecta)
                    if (moderadorActual?.email == correo.trim()) {
                        cargando = false
                        intentandoLoginModerador = false
                        Toast.makeText(
                            contexto,
                            "¡Bienvenido ${moderadorActual?.nombre} (moderador)!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navegarAPrincipalAdmin(moderadorActual?.id ?: "")
                        // Limpiar campos
                        correo = ""
                        clave = ""
                    }
                }
            }
            
            // Observar cuando el usuario se loguea exitosamente
            LaunchedEffect(usuarioActual, usuarioResult) {
                if (usuarioActual != null && 
                    usuarioResult is com.example.unilocal.utils.RequestResult.Sucess &&
                    !intentandoLoginModerador) {
                    cargando = false
                    intentandoLoginModerador = false
                    Toast.makeText(
                        contexto,
                        "¡Bienvenido ${usuarioActual?.nombre}!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navegarAPrincipalUsuario()
                    viewModel.resetear()
                    // Limpiar campos
                    correo = ""
                    clave = ""
                }
            }
            
            // Timeout: si después de intentar login de moderador no hay resultado, intentar como usuario
            LaunchedEffect(intentandoLoginModerador, moderadorActual) {
                if (intentandoLoginModerador && correo.isNotBlank() && clave.isNotBlank()) {
                    kotlinx.coroutines.delay(1500) // Esperar 1.5 segundos
                    // Si después de esperar no hay moderador, intentar como usuario
                    if (moderadorActual == null || moderadorActual?.email != correo.trim()) {
                        intentandoLoginModerador = false
                        viewModel.login(correo.trim(), clave)
                    }
                }
            }


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
