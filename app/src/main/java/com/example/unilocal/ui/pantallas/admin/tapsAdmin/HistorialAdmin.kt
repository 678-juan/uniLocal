package com.example.unilocal.ui.pantallas.admin.tapsAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.viewModel.ModeradorViewModel
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.ui.componentes.FichaLugarAdmin

@Composable
fun HistorialAdmin(
    moderadorViewModel: ModeradorViewModel = viewModel(),
    lugaresViewModel: LugaresViewModel = viewModel(),
    navegarALogin: () -> Unit = {}
) {
    val historial = moderadorViewModel.historial.collectAsState().value
    val lugares = lugaresViewModel.lugares.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // botón de cerrar sesión
            IconButton(
                onClick = {
                    moderadorViewModel.cerrarSesion()
                    navegarALogin()
                },
                modifier = Modifier
                    .background(
                        Color.Red.copy(alpha = 0.1f),
                        androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

        if (historial.isEmpty()) {
            Text("Sin registros todavía")
        } else {
            // primeros 5 registros
            historial.take(5).forEach { solicitud ->
                // buscar lugar
                val lugar = lugares.find { it.id == solicitud.lugarId }
                if (lugar != null) {
                    FichaLugarAdmin(
                        lugar = lugar,
                        estado = solicitud.accion.name,
                        onClick = { /* navegar a detalles si es necesario */ }
                    )
                }
            }

            // ver más
            if (historial.size > 5) {
                Text(
                    text = "... y ${historial.size - 5} registros más",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        }
    }
}



