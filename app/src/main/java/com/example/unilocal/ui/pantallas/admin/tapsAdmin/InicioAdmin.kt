package com.example.unilocal.ui.pantallas.admin.tapsAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.ModeracionViewModel
import com.example.unilocal.model.entidad.EstadoLugar

@Composable
fun InicioAdmin(
    lugaresViewModel: LugaresViewModel = viewModel(),
    moderacionViewModel: ModeracionViewModel = viewModel(),
    moderadorId: String
) {
    val lugares = lugaresViewModel.lugares.collectAsState().value
    val pendientes = lugares.filter { it.estado == EstadoLugar.PENDIENTE }
    val autorizadosPorMi = moderacionViewModel.misAutorizados.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Text(text = "Solicitudes pendientes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        pendientes.take(3).forEach { lugar ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(lugar.nombre, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.AUTORIZADO)
                                moderacionViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.AUTORIZADO)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) { Text("Autorizar") }

                        OutlinedButton(
                            onClick = {
                                lugaresViewModel.actualizarEstado(lugar.id, EstadoLugar.RECHAZADO)
                                moderacionViewModel.registrarDecision(lugar, moderadorId, EstadoLugar.RECHAZADO)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                        ) { Text("Rechazar") }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(text = "Autorizados por mí", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (autorizadosPorMi.isEmpty()) {
            Text("Aún no has autorizado lugares")
        } else {
            autorizadosPorMi.forEach { lugar ->
                ListItem(
                    headlineContent = { Text(lugar.nombre) },
                    supportingContent = { Text(lugar.categoria) }
                )
                Divider()
            }
        }
    }
}
