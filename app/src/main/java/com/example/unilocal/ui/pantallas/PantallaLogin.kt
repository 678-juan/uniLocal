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
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.LineaDecorativa
import com.example.unilocal.ui.theme.BlancoTexto


@Composable
fun PantallaLogin() {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

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
            Spacer(modifier = Modifier.height(250.dp))

            Spacer(modifier = Modifier.height(10.dp))
            Text(stringResource(R.string.login_title))

            Spacer(modifier = Modifier.height(32.dp))

            // C usuario
            CampoTexto(
                valor = usuario,
                cuandoCambia = { usuario = it },
                etiqueta = stringResource(R.string.usuario_hint),
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
                texto = stringResource(R.string.login_button),
                onClick = {  }
            )


            Spacer(modifier = Modifier.height(24.dp))

            // registrarse
            Text(
                text = stringResource(R.string.register_prompt),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = AzulEnlaces,
                modifier = Modifier.clickable { }
            )
        }
    }
}
