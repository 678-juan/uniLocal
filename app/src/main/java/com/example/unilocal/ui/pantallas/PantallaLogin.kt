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

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.ui.theme.AzulEnlaces
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.LineaDecorativa
import com.example.unilocal.ui.theme.BlancoTexto


@Composable
fun PantallaLogin()
{ var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // 🔹 Líneas arriba
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
            contentDescription = "Logo UniLocal",
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
            Text("Accede a tu cuenta")

            Spacer(modifier = Modifier.height(32.dp))

            // Campo usuario
            CampoTexto(
                valor = usuario,
                cuandoCambia = { usuario = it },
                etiqueta = "Usuario",
                modificador = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña
            CampoTexto(
                valor = clave,
                cuandoCambia = { clave = it },
                etiqueta = "Contraseña",
                modificador = Modifier.fillMaxWidth(),
                transformacion = PasswordVisualTransformation(),
                opcionesTeclado = KeyboardOptions.Default.copy()
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Recuperar contraseña
            Text(
                text = "Recupera tu contraseña",
                fontSize = 14.sp,
                color = AzulEnlaces,
                modifier = Modifier.clickable { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // boton entrar
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(

                    contentColor = BlancoTexto
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)


            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // registrarse
            Text(
                text = "¿No tienes cuenta? Regístrate",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = AzulEnlaces,
                modifier = Modifier.clickable { }
            )
        }
    }
}