package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.example.unilocal.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unilocal.ui.componentes.FichaLugar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.ui.componentes.FichaInformacion
import com.example.unilocal.ui.componentes.PublicacionUno
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel


@Composable
fun Recomendaciones(
    lugaresViewModel: LugaresViewModel,
    navegarALugar: (String) -> Unit,
    usuarioViewModel: UsuarioViewModel? = null
) {
    val lugares by lugaresViewModel.lugares.collectAsState()
    
    // solo lugares autorizados
    val lugaresAutorizados = lugares.filter { it.estado == EstadoLugar.AUTORIZADO }.take(10)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Tu ubicacion", fontWeight = FontWeight.Bold)

            Image(
                painter = painterResource(id = R.drawable.mapamomentaneo),
                contentDescription = "Mapa",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Recomendaciones cerca a tu ubicacion", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (lugaresAutorizados.isEmpty()) {
            item {
                Text(
                    text = "No hay recomendaciones disponibles en este momento",
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(lugaresAutorizados) { lugar ->
                PublicacionUno(
                    lugar = lugar,
                    onClick = { navegarALugar(lugar.id) },
                    usuarioViewModel = usuarioViewModel
                )
            }
        }
    }
}








