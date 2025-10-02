package com.example.unilocal.ui.pantallas.usuario.tapsUsuario
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.ui.componentes.FichaInformacion

import com.example.unilocal.ui.componentes.PublicacionUno
import com.example.unilocal.viewModel.LugaresViewModel

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun LugarDetalles(
    idLugar: String,
    navegacionARecomendados: () -> Unit
) {
    val lugarViewModel: LugaresViewModel = viewModel()
    val lugar: Lugar? = lugarViewModel.buscarPorId(idLugar)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                modifier = Modifier.clickable { navegacionARecomendados() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Más información del lugar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        lugar?.let {
            PublicacionUno(
                lugar = it,
                onClick = { }
            )

            Spacer(modifier = Modifier.height(12.dp))

            FichaInformacion(
                lugar = it,
            )
        }
    }
}
