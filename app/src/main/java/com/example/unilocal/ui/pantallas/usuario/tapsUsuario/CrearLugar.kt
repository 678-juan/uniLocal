package com.example.unilocal.ui.pantallas.usuario.tapsUsuario


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.R

import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.CampoTexto

@Composable
fun CrearLugar() {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val categorias = listOf(
        stringResource(R.string.categoria_restaurante),
        stringResource(R.string.categoria_cafe),
        stringResource(R.string.categoria_hotel),
        stringResource(R.string.categoria_museo),
        stringResource(R.string.categoria_comida_rapida),
        stringResource(R.string.categoria_pasada)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .size(190.dp)

        )


        Text(
            text = stringResource(R.string.crea_nuevo_hogar),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)

        // categoria
        Text(
            text = stringResource(R.string.selecciona_categoria),
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.forEach { cat ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color(0xFFFFEB3B), shape = RoundedCornerShape(50))
                        .border(2.dp, Color.DarkGray, shape = RoundedCornerShape(50))
                        .clickable { /* seleccionar categoría */ }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = cat, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos de texto
        CampoTexto(
            valor = nombre,
            cuandoCambia = { nombre = it },
            etiqueta = stringResource(R.string.nombre_lugar)
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = descripcion,
            cuandoCambia = { descripcion = it },
            etiqueta = stringResource(R.string.descripcion_lugar)
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = horario,
            cuandoCambia = { horario = it },
            etiqueta = stringResource(R.string.horario_atencion)
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = telefono,
            cuandoCambia = { telefono = it },
            etiqueta = stringResource(R.string.telefono_lugar)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CampoTexto(
                valor = direccion,
                cuandoCambia = { direccion = it },
                etiqueta = stringResource(R.string.marca_direccion),
                modificador = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /* abrir mapa */ },
                modifier = Modifier.align(Alignment.Bottom)
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar en mapa")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // mapa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Mapa de Google (placeholder)")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // galeria
        Text(
            text = stringResource(R.string.selecciona_foto),
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Galería de fotos (placeholder)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        BotonPrincipal(
            texto = stringResource(R.string.boton_guardar),
            onClick = { /* guardar */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}
