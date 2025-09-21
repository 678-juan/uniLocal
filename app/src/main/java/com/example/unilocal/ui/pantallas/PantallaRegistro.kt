package com.example.unilocal.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.unilocal.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.ui.componentes.LineaDecorativa


@Composable
fun PantallaRegisto(
    onRegistrar: (nombre: String, username: String, email: String, ciudad: String, password: String) -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            contentDescription = stringResource(R.string.app_name), // o "Logo UniLocal"
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
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = stringResource(R.string.register_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            CampoTexto(
                valor = nombre,
                cuandoCambia = { nombre = it },
                etiqueta = stringResource(R.string.nombre_hint)
            )
            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = username,
                cuandoCambia = { username = it },
                etiqueta = stringResource(R.string.username_hint)
            )
            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = email,
                cuandoCambia = { email = it },
                etiqueta = stringResource(R.string.email_hint),
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = ciudad,
                cuandoCambia = { ciudad = it },
                etiqueta = stringResource(R.string.ciudad_hint)
            )
            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = password,
                cuandoCambia = { password = it },
                etiqueta = stringResource(R.string.password_hint),
                transformacion = PasswordVisualTransformation(),
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(24.dp))

            BotonPrincipal(
                texto = stringResource(R.string.register_button),
                onClick = {
                    onRegistrar(nombre, username, email, ciudad, password)
                }
            )
        }
    }
}