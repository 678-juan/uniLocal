package com.example.unilocal.ui.pantallas.admin.tapsAdmin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeracionViewModel
import com.example.unilocal.model.entidad.EstadoLugar

@Composable
fun SolicitudesAdmin(
    lugaresViewModel: LugaresViewModel = viewModel(),
    moderacionViewModel: ModeracionViewModel = viewModel(),
    moderadorId: String
) {
    val pendientes = lugaresViewModel.lugares.collectAsState().value
        .filter { it.estado == EstadoLugar.PENDIENTE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Solicitudes", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        pendientes.forEach { lugar ->
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(12.dp)) {
                    if (lugar.imagenResId != 0) {
                        Image(
                            painter = painterResource(id = lugar.imagenResId),
                            contentDescription = lugar.nombre,
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(lugar.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(lugar.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = {
                            lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.AUTORIZADO)
                            moderacionViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.AUTORIZADO)
                        }) { Text("Aceptar") }

                        OutlinedButton(onClick = {
                            lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.RECHAZADO)
                            moderacionViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.RECHAZADO)
                        }) { Text("Rechazar") }
                    }
                }
            }
        }
    }
}


